package enginecrafter77.survivalinc.stats.impl;

import com.google.common.collect.ImmutableSet;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
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

public class WaterVolume implements INBTSerializable<NBTTagCompound> {
	
	private static Set<Biome> saltybiomes;
	private static Set<Biome> dirtybiomes;
	private static boolean initialized = false;
	
	private int volume;
	private float salinity;
	private float temperature;
	private boolean dirty;
	
	public WaterVolume(int volume, float salinity, float temperature, boolean dirty)
	{
		this.volume = volume;
		this.salinity = salinity;
		this.temperature = temperature;
		this.dirty = dirty;
	}
	
	public int getVolume()
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
	
	public void setVolume(int volume)
	{
		this.volume = volume;
	}
	
	public void clear()
	{
		this.volume = 0;
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
		int newvolume = this.volume + other.volume;
		this.temperature = (this.temperature * this.volume + other.temperature * other.volume) / (float)newvolume;
		this.salinity = (this.getSaltAmount() + other.getSaltAmount()) / (float)newvolume;
		this.dirty = this.dirty || other.dirty;
		this.volume = newvolume;
	}
	
	public WaterVolume split(int amount)
	{
		if(amount > this.volume)
		{
			amount = this.volume;
			this.clear();
		}
		else this.volume -= amount;
		
		return new WaterVolume(amount, this.salinity, this.temperature, this.dirty);
	}

	public void consume(EntityLivingBase entity)
	{
		if(!entity.world.isRemote)
		{
			SurvivalInc.logger.error("WWWL", new RuntimeException());

			if(this.salinity > 0.02F)
				entity.attackEntityFrom(DamageSource.GENERIC, 5F);

			if(this.dirty)
				entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
		}

		StatCapability.obtainRecord(SurvivalInc.hydration, entity).ifPresent((SimpleStatRecord record) -> record.addToValue(this.getHydrationBonus()));

		StatCapability.obtainRecord(SurvivalInc.heat, entity).ifPresent((SimpleStatRecord heat) -> {
			if((20F + this.temperature * 20F) > heat.getValue()) heat.addToValue(this.temperature * 10F);
			else heat.addToValue(this.temperature * -10F);
		});
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
		this.volume = nbt.getInteger("volume");
		this.salinity = nbt.getFloat("salinity");
		this.temperature = nbt.getFloat("temperature");
		this.dirty = nbt.getBoolean("dirty");
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

	public static WaterVolume empty()
	{
		return new WaterVolume(0, 0F, 0F, false);
	}

	public static WaterVolume fromNBT(NBTTagCompound tag)
	{
		WaterVolume volume = WaterVolume.empty();
		volume.deserializeNBT(tag);
		return volume;
	}
	
	public static WaterVolume fromBlock(IBlockAccess world, BlockPos position, int amount)
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
