package enginecrafter77.survivalinc.stats.impl;

import com.google.common.collect.ImmutableSet;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.effect.StatEffect;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;

public class WaterVolume implements StatEffect<SimpleStatRecord>, INBTSerializable<NBTTagCompound> {
	
	private static Set<Biome> saltybiomes;
	private static Set<Biome> dirtybiomes;
	private static boolean initialized = false;
	
	private float volume;
	private float salinity;
	private float temperature;
	private boolean dirty;
	
	public WaterVolume(NBTTagCompound tag)
	{
		this(0F, 0F, 0F, false);
		this.deserializeNBT(tag);
	}
	
	public WaterVolume(float volume, float salinity, float temperature, boolean dirty)
	{
		this.volume = volume;
		this.salinity = salinity;
		this.temperature = temperature;
		this.dirty = dirty;
	}
	
	public float getVolume()
	{
		return this.volume;
	}
	
	public float getSalinity()
	{
		return this.salinity;
	}
	
	public float getTemperature()
	{
		return this.temperature;
	}
	
	public boolean isDirty()
	{
		return this.dirty;
	}
	
	public void setVolume(float volume)
	{
		this.volume = volume;
	}
	
	public void empty()
	{
		this.volume = 0F;
		this.salinity = 0F;
		this.temperature = 0F;
		this.dirty = false;
	}
	
	protected float getSaltAmount()
	{
		return this.volume * this.salinity;
	}
	
	public void mix(WaterVolume other)
	{
		float newvolume = this.volume + other.volume;
		this.temperature = (this.temperature * this.volume + other.temperature * other.volume) / newvolume;
		this.salinity = (this.getSaltAmount() + other.getSaltAmount()) / newvolume;
		this.dirty = this.dirty || other.dirty;
		this.volume = newvolume;
	}
	
	public WaterVolume remove(float amount)
	{
		if(amount > this.volume)
		{
			amount = this.volume;
			this.empty();
		}
		else this.volume -= amount;
		
		return new WaterVolume(amount, this.salinity, this.temperature, this.dirty);
	}
	
	public float getHydrationBonus()
	{
		return this.volume * (1F - this.salinity / 0.03F);
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("volume", this.volume);
		tag.setFloat("salinity", this.salinity);
		tag.setFloat("temperature", this.temperature);
		tag.setBoolean("dirty", this.dirty);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.volume = nbt.getFloat("volume");
		this.salinity = nbt.getFloat("salinity");
		this.temperature = nbt.getFloat("temperature");
		this.dirty = nbt.getBoolean("dirty");
	}
	
	@Override
	public void apply(SimpleStatRecord record, EntityPlayer player)
	{
		record.addToValue(this.getHydrationBonus());
		
		if(this.salinity > 0.02F) player.attackEntityFrom(DamageSource.GENERIC, 1F);

		StatCapability.obtainRecord(SurvivalInc.heat, player).ifPresent((SimpleStatRecord heat) -> {
			if((20F + this.temperature * 20F) > heat.getValue()) heat.addToValue(this.temperature * 10F);
			else heat.addToValue(this.temperature * -10F);
		});
		
		if(this.dirty) player.addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof WaterVolume)
		{
			WaterVolume othervolume = (WaterVolume)other;
			return this.volume == othervolume.volume && this.dirty == othervolume.dirty && this.salinity == othervolume.salinity && this.temperature == othervolume.temperature;
		}
		return false;
	}
	
	public static void checkTables()
	{
		if(WaterVolume.initialized) return;
		WaterVolume.saltybiomes = ImmutableSet.of(Biomes.BEACH, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.MUSHROOM_ISLAND_SHORE);
		WaterVolume.dirtybiomes = ImmutableSet.of(Biomes.MUSHROOM_ISLAND, Biomes.SWAMPLAND, Biomes.MUTATED_SWAMPLAND);
		WaterVolume.initialized = true;
	}
	
	public static WaterVolume fromBlock(IBlockAccess world, BlockPos position, float amount)
	{
		WaterVolume.checkTables();
		
		IBlockState blockstate = world.getBlockState(position);
		if(blockstate.getMaterial() == Material.WATER)
		{
			float salinity = 0F;
			boolean dirty = false;
			
			Biome biome = world.getBiome(position);
			if(WaterVolume.saltybiomes.contains(biome)) salinity += 0.035F;
			if(WaterVolume.dirtybiomes.contains(biome)) dirty = true;
			
			return new WaterVolume(amount, salinity, biome.getDefaultTemperature(), dirty);
		}
		return null;
	}
}
