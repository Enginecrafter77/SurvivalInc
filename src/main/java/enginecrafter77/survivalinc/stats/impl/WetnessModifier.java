package enginecrafter77.survivalinc.stats.impl;

import java.util.Random;
import java.util.UUID;
import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.SideEffectFilter;
import enginecrafter77.survivalinc.stats.effect.ValueStatEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/*
 * This is where the magic of changing one's wetness occurs. You'll most likely be here.
 * Geez that sounds wrong. I wonder if Klei implemented their wetness mechanic with dumb jokes too.
 * This is also the first mechanic I implemented, so some old code will most likely be here.
 * The newest is temperature, so that'll look more functional.
 * 	-Schoperation
 * 
 * Although this class has been rewritten from the ground up, I still wanted to
 * keep the schoperation's comment above. I find it funny.
 */

public class WetnessModifier implements StatProvider<SimpleStatRecord> {
	private static final long serialVersionUID = -4227255838351827965L;
	
	public static WetnessModifier instance = null;
	
	public final EffectApplicator<SimpleStatRecord> effects;
	public final UUID wetnessSlowdown;
	
	public WetnessModifier()
	{
		this.wetnessSlowdown = UUID.nameUUIDFromBytes(this.getStatID().toString().getBytes());
		this.effects = new EffectApplicator<SimpleStatRecord>();
		
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, 0.01F)).addFilter(FunctionalEffectFilter.byPlayer((EntityPlayer player) -> player.world.isRainingAt(player.getPosition().up())));
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.8F)).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isBurning));
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.08F)).addFilter(HydrationModifier.isOutsideOverworld);
		
		this.effects.add(WetnessModifier::scanSurroundings);
		this.effects.add(WetnessModifier::naturalDrying).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isWet).invert());
		this.effects.add(WetnessModifier::whenInWater).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isInWater));
		this.effects.add(WetnessModifier::slowDown).addFilter(SideEffectFilter.SERVER);
	}
	
	public static void init()
	{
		WetnessModifier.instance = new WetnessModifier();
		MinecraftForge.EVENT_BUS.register(WetnessModifier.class);
	}
	
	/**
	 * A simple method to check whether the provider was loaded or not.
	 * This should coincide with whether the provider is registered in
	 * the player's stat registry. This should NOT be confused with {@link enginecrafter77.survivalinc.config.WetnessConfig#enabled},
	 * since the latter can be changed during the game.
	 * @return True if the {@link #init()} method has been called in the past, false otherwise.
	 */
	public static boolean loaded()
	{
		return WetnessModifier.instance != null;
	}
	
	@Override
	public void update(EntityPlayer target, SimpleStatRecord wetness)
	{
		this.effects.apply(wetness, target);
		wetness.checkoutValueChange();
	}

	@Override
	public ResourceLocation getStatID()
	{
		return new ResourceLocation(SurvivalInc.MOD_ID, "wetness");
	}

	@Override
	public SimpleStatRecord createNewRecord()
	{
		return new SimpleStatRecord(Range.closed(0F, 100F));
	}
	
	@Override
	public Class<SimpleStatRecord> getRecordClass()
	{
		return SimpleStatRecord.class;
	}
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(WetnessModifier.instance);
	}
	
	public static void slowDown(SimpleStatRecord record, EntityPlayer player)
	{
		// Ugh I really hate this code. It's damn ineffective. So much list IO to handle every single tick. In any case, remove the modifier.
		IAttributeInstance inst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		inst.removeModifier(WetnessModifier.instance.wetnessSlowdown);
		
		// Check if there is actually a floor to put up contradicting force acting against gravity.
		IBlockState ground = player.world.getBlockState(player.getPosition().down());
		if(!(ground.getBlock() instanceof BlockLiquid || player.isInWater())) // Or, if the player is simply in water.
		{
			// This is the math part. I am way less worried about impact of this. Mmmm math...
			float mod = (float)(-Math.pow(record.getNormalizedValue(), 0.333333333F) * (1D - ModConfig.WETNESS.minimalWalkSpeed));
			
			// Squash the modifier if it's too small to avoid the Aristotle's Turtle problem
			if(Math.log10(mod) < -3) mod = 0F;
			
			// Apply the modifier
			inst.applyModifier(new AttributeModifier(WetnessModifier.instance.wetnessSlowdown, "wetnessSlowdown", mod, 2).setSaved(false));
		}
	}
	
	public static void whenInWater(SimpleStatRecord record, EntityPlayer player)
	{
		Material headBlockMaterial = player.world.getBlockState(new BlockPos(player).up()).getMaterial();
		if(headBlockMaterial == Material.WATER) record.addToValue((float)ModConfig.WETNESS.fullySubmergedRate);
		else if(record.getNormalizedValue() < ModConfig.WETNESS.partiallySubmergedCap) record.addToValue((float)ModConfig.WETNESS.partiallySubmergedRate);
	}
	
	public static void naturalDrying(SimpleStatRecord record, EntityPlayer player)
	{
		// Subdivision-based draining; Always drains a fixed proportion based on the current value, producing a 1/x like curve.
		float difference = record.getValue() / (float)ModConfig.WETNESS.drainingFactor;
		record.addToValue(-difference);
		
		// Spawn the particles on client side
		if(player.world.isRemote)
		{
			WorldClient world = (WorldClient)player.world;
			Random rng = world.rand;
			for(int index = Math.round(4F * difference); index > 0; index--)
			{
				world.spawnParticle(EnumParticleTypes.DRIP_WATER, player.posX + (rng.nextFloat() * 0.5 - 0.25), player.posY + (rng.nextFloat() * 1 + 0.25), player.posZ + (rng.nextFloat() * 0.5 - 0.25), player.motionX, -0.5D, player.motionZ);
			}
		}
	}
	
	public static void scanSurroundings(SimpleStatRecord record, EntityPlayer player)
	{
		Vec3i offset = new Vec3i(2, 1, 2);
		BlockPos origin = new BlockPos(player);
		
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(origin.subtract(offset), origin.add(offset));
		
		float diff = 0;
		for(BlockPos position : blocks)
		{
			Block block = player.world.getBlockState(position).getBlock();
			if(HeatModifier.instance.blockHeatMap.containsKey(block))
			{
				float basewetness = HeatModifier.instance.blockHeatMap.get(block) / -400F;
				float proximity = (float)Math.sqrt(position.distanceSq(player.posX, player.posY, player.posZ));
				diff += basewetness / proximity;
			}
		}
		if(player.isWet()) diff /= 2F;
		record.addToValue(diff);
	}
}