package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.SideEffectFilter;
import enginecrafter77.survivalinc.stats.effect.ValueStatEffect;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
	
	public static final WetnessModifier instance = new WetnessModifier();
	
	public final Map<Block, Float> humiditymap = new HashMap<Block, Float>();
	public final EffectApplicator<SimpleStatRecord> effects;
	public final UUID wetnessSlowdown;
	
	public WetnessModifier()
	{
		this.wetnessSlowdown = UUID.nameUUIDFromBytes(this.getStatID().toString().getBytes());
		this.effects = new EffectApplicator<SimpleStatRecord>();
	}
	
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(WetnessModifier.class);
		
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, 0.01F)).addFilter(FunctionalEffectFilter.byPlayer((EntityPlayer player) -> player.world.isRainingAt(player.getPosition().up())));
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.8F)).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isBurning));
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.08F)).addFilter(HydrationModifier.isOutsideOverworld);
		
		this.effects.add(WetnessModifier::causeDripping).addFilter(SideEffectFilter.CLIENT);
		this.effects.add(WetnessModifier::slowDown).addFilter(SideEffectFilter.SERVER);
		this.effects.add(WetnessModifier::scanSurroundings);
		this.effects.add(WetnessModifier::naturalDrying);
		this.effects.add(WetnessModifier::whenInWater);
		
		this.humiditymap.put(Blocks.FIRE, -0.5F);
		this.humiditymap.put(Blocks.LAVA, -1F);
		this.humiditymap.put(Blocks.FLOWING_LAVA, -1F);
		this.humiditymap.put(Blocks.LIT_FURNACE, -0.4F);
		this.humiditymap.put(Blocks.MAGMA, -0.4F);
	}
	
	@Override
	public void update(EntityPlayer target, StatRecord record)
	{
		SimpleStatRecord wetness = (SimpleStatRecord)record;
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
		SimpleStatRecord record = new SimpleStatRecord();
		record.setValueRange(Range.closed(0F, 100F));
		return record;
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
		float current = record.getValue(), max = record.getValueRange().upperEndpoint();
		float threshold = (float)ModConfig.WETNESS.slowdownThreshold / 100F;
		
		// This is the math part. I am way less worried about impact of this. Mmmm math...
		float mod = 0F;
		if(current > (threshold * max))
		{
			/*
			 * We want to achieve following scenario:
			 * 	When the wetness is just at the slowdown threshold, apply zero slowdown (closest 0)
			 * 	As the wetness is rising, slow the player down in linear manner (which implies using direct relationship function)
			 * 	When the wetness is at maximum, the value should be just above -1 (to avoid total nullification of the speed)
			 * After some experimentation and calculations, I came up with this little equation:
			 * 	      1 - g           g - 1
			 * 	y = ---------- x + t -------
			 * 	     m(t - 1)         t - 1
			 * It may seem a little bit complicated, but you would get to that anyways if you would try to solve it.
			 */
			float correction = (float)ModConfig.WETNESS.minimalWalkSpeed;
			float inclination = (1 - correction) / (max * (threshold - 1)); // The inclination of the graph, aka the A
			float offset = threshold * ((correction - 1) / (threshold - 1)); // The offset of the graph, aka the B
			mod += inclination * current + offset; // Direct relationship formula
		}
		
		if(player.isInWater()) mod *= (float)ModConfig.WETNESS.submergedSlowdownFactor;
		
		// Ugh I really hate this code. It's damn ineffective. So much list IO to handle every single tick.
		IAttributeInstance inst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		inst.removeModifier(WetnessModifier.instance.wetnessSlowdown);
		inst.applyModifier(new AttributeModifier(WetnessModifier.instance.wetnessSlowdown, "wetnessSlowdown", mod, 1).setSaved(false));
	}
	
	/**
	 * Causes client-side dripping effect
	 * @param player The player to apply for
	 * @param current The current level of wetness
	 */
	public static void causeDripping(SimpleStatRecord record, EntityPlayer player)
	{
		WorldClient world = (WorldClient)player.world;
		Random rng = world.rand;
		for(int index = Math.round(4F * record.getNormalizedValue()); index > 0; index--)
		{
			world.spawnParticle(EnumParticleTypes.DRIP_WATER, player.posX + (rng.nextFloat() * 0.5 - 0.25), player.posY + (rng.nextFloat() * 1 + 0.25), player.posZ + (rng.nextFloat() * 0.5 - 0.25), player.motionX, -0.5, player.motionZ);
		}
	}
	
	public static void whenInWater(SimpleStatRecord record, EntityPlayer player)
	{
		if(player.isInWater())
		{
			Material headBlockMaterial = player.world.getBlockState(new BlockPos(player).up()).getMaterial();
			if(headBlockMaterial == Material.WATER) record.addToValue(5F);
			else if(record.getNormalizedValue() < 0.4F) record.addToValue(1.25F);
		}
	}
	
	public static void naturalDrying(SimpleStatRecord record, EntityPlayer player)
	{
		if(!player.isWet())
		{
			float rate = (float)ModConfig.WETNESS.passiveDryRate;
			if(player.world.isDaytime() && player.world.canBlockSeeSky(player.getPosition())) rate *= ModConfig.WETNESS.sunlightMultiplier;
			record.addToValue(-rate);
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
			if(WetnessModifier.instance.humiditymap.containsKey(block))
			{
				float basewetness = WetnessModifier.instance.humiditymap.get(block);
				float proximity = (float)Math.sqrt(position.distanceSq(player.posX, player.posY, player.posZ));
				diff += basewetness / proximity;
			}
		}
		if(player.isWet()) diff /= 2F;
		record.addToValue(diff);
	}
}