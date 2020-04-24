package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.WaterDrinkMessage;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.DamagingModifier;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.OperationType;
import enginecrafter77.survivalinc.stats.modifier.PotionEffectModifier;
import enginecrafter77.survivalinc.stats.modifier.ThresholdModifier;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HydrationModifier implements IMessageHandler<WaterDrinkMessage, IMessage> {
	
	public static final DamageSource DEHYDRATION = new DamageSource("survivalinc_dehydration").setDamageIsAbsolute().setDamageBypassesArmor();
	
	public static void init()
	{
		DefaultStats.HYDRATION.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.dimension == -1, -0.006F), OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.isInLava(), -0.5F), OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.add(new FunctionalModifier<EntityPlayer>(HydrationModifier::naturalDrain), OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new DamagingModifier(DEHYDRATION, 4F, 0), 5F, ThresholdModifier.LOWER));
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.NAUSEA, 5), 15F, ThresholdModifier.LOWER));
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.MINING_FATIGUE, 3), 15F, ThresholdModifier.LOWER));
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.SLOWNESS, 3), 40F, ThresholdModifier.LOWER));
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.WEAKNESS, 2), 40F, ThresholdModifier.LOWER));
	}
	
	public static float naturalDrain(EntityPlayer player, float value)
	{
		float drain = -(float)ModConfig.HYDRATION.passiveDrain;
		if(ModConfig.HEAT.enabled)
		{
			StatProvider heat = HeatModifier.instance;
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			if(((tracker.getStat(heat) - heat.getMinimum()) / (heat.getMaximum() - heat.getMinimum())) > ModConfig.HYDRATION.sweatingThreshold) drain *= ModConfig.HYDRATION.sweatingMultiplier;
		}
		return drain;
	}
	
	/**
	 * This method gets run on server to verify the client's drink
	 * request and apply the effects if valid. If the player's request
	 * was fake (artificially created without client-side ray trace),
	 * it will get detected and log it as warning to the server console.
	 */
	@Override
	public IMessage onMessage(WaterDrinkMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		WorldServer world = player.getServerWorld();
		
		// Verify if the client message is not fake (i.e. to artificially increase hydration)
		RayTraceResult water_rt = raytraceWaterDrinking(player, message.getHand());
		if(water_rt != null)
		{
			SurvivalInc.logger.info("Player drink request authorized.");
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			tracker.modifyStat(DefaultStats.HYDRATION, (float)ModConfig.HYDRATION.drinkAmount);
			HydrationModifier.spawnWaterDrinkParticles(world, water_rt.hitVec);
		}
		else SurvivalInc.logger.warn("Player {}'s client issued a fake WaterDrinkMessage.");
		return null;
	}
	
	
	/**
	 * This method gets run only on client. It basically sends "water drink request"
	 * to the server. This approach was chosen since this method is run only on client,
	 * and since the client can easily be modified to artificially create these packets,
	 * it is therefore always validated on the server side. 
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent.RightClickEmpty event)
	{
		if(event.getHand() != EnumHand.MAIN_HAND) return; // We do not want to run this twice
		
		EntityPlayer player = event.getEntityPlayer();
		if(player.isCreative() || player.isSpectator()) return;
		
		// Do the raytrace
		RayTraceResult water_rt = raytraceWaterDrinking(player, event.getHand());
		if(water_rt != null)
		{
			// Modify the client tracker
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			tracker.modifyStat(DefaultStats.HYDRATION, (float)ModConfig.HYDRATION.drinkAmount);
			SurvivalInc.proxy.net.sendToServer(new WaterDrinkMessage(event.getHand()));
		}
	}
	
	/**
	 * This event gets fired on both sides, so it's not necessary to get
	 * server authorization regarding the drinking.
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getHand() != EnumHand.MAIN_HAND) return; // We do not want to run this twice nor on server side
		
		EntityPlayer player = event.getEntityPlayer();
		if(player.isCreative() || player.isSpectator()) return;
		
		// Do the raytrace
		RayTraceResult water_rt = raytraceWaterDrinking(player, event.getHand());
		if(water_rt != null)
		{
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			tracker.modifyStat(DefaultStats.HYDRATION, (float)ModConfig.HYDRATION.drinkAmount);
			if(!player.world.isRemote) HydrationModifier.spawnWaterDrinkParticles((WorldServer)player.world, water_rt.hitVec);
		}
	}
	
	/**
	 * Returns the raytrace result if the water drinking conditions were met, or null otherwise.
	 * @param player The player to do the raytrace from
	 * @param hand The hand used during the raytrace
	 * @return RayTraceResult if the drink conditions are met, null otherwise
	 */
	private static RayTraceResult raytraceWaterDrinking(EntityPlayer player, EnumHand hand)
	{
		if(player.getHeldItem(hand).isEmpty())
		{			
			Vec3d look = player.getPositionEyes(1.0f).add(player.getLookVec().add(player.getLookVec().x < 0 ? -0.5 : 0.5, -1, player.getLookVec().z < 0 ? -0.5 : 0.5));
			RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(1.0f), look, true);
			if(raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				BlockPos position = raytrace.getBlockPos();
				if(player.world.getBlockState(position).getMaterial() == Material.WATER)
				{
					return raytrace;
				}
			}
		}
		return null;
	}
	
	private static void spawnWaterDrinkParticles(WorldServer world, Vec3d position)
	{
		world.spawnParticle(EnumParticleTypes.WATER_SPLASH, position.x, position.y + 0.1, position.z, 20, 0.5, 0.1, 0.5, 0.1, null);
		world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, position.x, position.y + 0.1, position.z, 20, 0.5, 0.1, 0.5, 0.1, null);
		world.playSound(null, new BlockPos(position), SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.AMBIENT, 0.5f, 1.5f);
	}
}
