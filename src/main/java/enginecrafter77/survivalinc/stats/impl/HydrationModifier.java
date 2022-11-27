package enginecrafter77.survivalinc.stats.impl;

import com.google.common.collect.Range;
import enginecrafter77.survivalinc.ClientProxy;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.AbsoluteElementLayoutFunction;
import enginecrafter77.survivalinc.client.HUDConstructEvent;
import enginecrafter77.survivalinc.client.ScaleRenderFilter;
import enginecrafter77.survivalinc.client.StatFillBar;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.WaterDrinkMessage;
import enginecrafter77.survivalinc.stats.*;
import enginecrafter77.survivalinc.stats.effect.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Rectangle;

import javax.annotation.Nullable;

public class HydrationModifier implements StatProvider<SimpleStatRecord> {
	public static final EffectFilter<StatRecord> IS_OUTSIDE_OVERWORLD = FunctionalEffectFilter.byPlayer((EntityPlayer player) -> player.dimension != 0);
	public static final DamageSource DEHYDRATION = new DamageSource("survivalinc_dehydration").setDamageIsAbsolute().setDamageBypassesArmor();

	public final EffectApplicator<SimpleStatRecord> effects;
	
	public HydrationModifier()
	{
		this.effects = new EffectApplicator<SimpleStatRecord>();
		
		EffectFilter<SimpleStatRecord> fatique = FunctionalEffectFilter.byValue(Range.lessThan(10F));
		EffectFilter<SimpleStatRecord> slowness = FunctionalEffectFilter.byValue(Range.lessThan(20F));
		
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.006F)).addFilter(HydrationModifier.IS_OUTSIDE_OVERWORLD);
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.5F)).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isInLava));
		this.effects.add(new DamageStatEffect(HydrationModifier.DEHYDRATION, 4F, 0)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(5F)));
		this.effects.add(new PotionStatEffect(MobEffects.SLOWNESS, 5)).addFilter(slowness);
		this.effects.add(new PotionStatEffect(MobEffects.WEAKNESS, 5)).addFilter(slowness);
		this.effects.add(new PotionStatEffect(MobEffects.MINING_FATIGUE, 5)).addFilter(fatique);
		this.effects.add(new PotionStatEffect(MobEffects.NAUSEA, 5)).addFilter(fatique);
		this.effects.add(this::naturalDrain);
	}

	@Override
	public void update(EntityPlayer target, SimpleStatRecord hydration)
	{
		this.effects.apply(hydration, target);
		hydration.checkoutValueChange();
	}

	@Override
	public ResourceLocation getStatID()
	{
		return new ResourceLocation(SurvivalInc.MOD_ID, "hydration");
	}

	@Override
	public SimpleStatRecord createNewRecord()
	{
		return new SimpleStatRecord(Range.closed(0F, 100F));
	}

	@Override
	public void resetRecord(SimpleStatRecord record)
	{
		record.setValue((float)ModConfig.HYDRATION.startValue);
	}

	@Override
	public Class<SimpleStatRecord> getRecordClass()
	{
		return SimpleStatRecord.class;
	}
	
	/**
	 * This method gets run on server to verify the client's drink
	 * request and apply the effects if valid. If the player's request
	 * was fake (artificially created without client-side ray trace),
	 * it will get detected and log it as warning to the server console.
	 */
	public static IMessage validateMessage(WaterDrinkMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		WorldServer world = player.getServerWorld();
		WaterVolume volume = message.getWaterVolume();
		Vec3d hitvec = message.getHitPosition();
		
		String ip = ctx.getServerHandler().getNetworkManager().getRemoteAddress().toString();
		if(ModConfig.GENERAL.verifyClientDrinkingRequests)
		{
			RayTraceResult water_rt = HydrationModifier.raytraceWaterDrinking(player, message.getHand());
			if(water_rt == null)
			{
				SurvivalInc.logger.warn("Water drink packet received (from: {}), but no body of water found :/", ip);
				return null;
			}
			else
			{
				WaterVolume verifiedvolume = WaterVolume.fromBlock(player.world, water_rt.getBlockPos(), (float)ModConfig.HYDRATION.sipVolume);
				if(!volume.equals(verifiedvolume))
				{
					SurvivalInc.logger.warn("Server raytrace results not consistent with client's claim. Fabricated packet from {}?", ip);
					return null;
				}
				
				hitvec = water_rt.hitVec;
				volume = verifiedvolume;
			}
		}
		else if(volume == null || hitvec == null)
		{
			SurvivalInc.logger.error("Invalid water volume in request from {}.", ip);
			return null;
		}

		HydrationModifier.spawnWaterDrinkParticles(world, hitvec);
		volume.consume(player);
		return null;
	}
	
	public void naturalDrain(SimpleStatRecord record, EntityPlayer player)
	{
		StatCapability.obtainRecord(SurvivalInc.heat, player).ifPresent((SimpleStatRecord heat) -> {
			float drain = (float)ModConfig.HYDRATION.passiveDrain;
			if(heat.getNormalizedValue() > ModConfig.HYDRATION.sweatingThreshold)
				drain *= ModConfig.HYDRATION.sweatingMultiplier;
			record.addToValue(-drain);
		});
	}
	
	@SubscribeEvent
	public void registerStat(StatRegisterEvent event)
	{
		event.register(this);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void constructHud(HUDConstructEvent event)
	{
		StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(this, ModConfig.CLIENT.hud.hydrationBarDirection, ClientProxy.STAT_ICONS.region(new Rectangle(0, 9, 9, 9)));
		bar.addLayer(ClientProxy.STAT_ICONS.region(new Rectangle(9, 9, 9, 9)), SimpleStatRecord::getNormalizedValue);
		bar.setCapacity(ModConfig.CLIENT.hud.hydrationBarCapacity);
		bar.setSpacing(ModConfig.CLIENT.hud.hydrationBarSpacing);

		if(ModConfig.CLIENT.hud.stackHydrationBar)
			event.addElement(bar, ModConfig.CLIENT.hud.hydrationBarStack).setTrigger(ModConfig.CLIENT.hud.hydrationBarRenderTrigger).addFilter(ClientProxy.TEXTURE_RESET_FILTER);
		else
			event.addElement(bar, new AbsoluteElementLayoutFunction((float)ModConfig.CLIENT.hud.originX, (float)ModConfig.CLIENT.hud.originY, ModConfig.CLIENT.hud.hydrationBarX, ModConfig.CLIENT.hud.hydrationBarY));

		if(ModConfig.CLIENT.vignette.enable)
			event.addElement(new StatRangeVignette(this, Range.lessThan(30F), ClientProxy.parseColor(ModConfig.CLIENT.vignette.dehydrationColor), ModConfig.CLIENT.vignette.maxOpacity, ModConfig.CLIENT.vignette.logarithmicOpacity, true), AbsoluteElementLayoutFunction.ORIGIN).addFilter(new ScaleRenderFilter(Vec2f.MAX));
	}
	
	/**
	 * This method gets run only on client. It basically sends "water drink request"
	 * to the server. This approach was chosen since this method is run only on client,
	 * and since the client can easily be modified to artificially create these packets,
	 * it is therefore always validated on the server side.
	 * Essentially, this method differs from {@link #onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock)}
	 * by only that when the player click on water without reading for a block underneath it, then
	 * this event is issued since it's treated like the player clicked with empty hand in the air.
	 * @param event The empty hand interaction event
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerRightClick(PlayerInteractEvent.RightClickEmpty event)
	{
		if(HydrationModifier.isDrinkEventInvalid(event)) return;
		
		WaterDrinkMessage result = this.tryDrink(event.getEntityPlayer(), event.getHand());
		if(result != null) SurvivalInc.proxy.net.sendToServer(result);
	}
	
	/**
	 * This event gets fired on both sides, so it's not necessary to get
	 * server authorization regarding the drinking.
	 * @param event The block click event
	 */
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
	{
		if(HydrationModifier.isDrinkEventInvalid(event)) return;
		
		EntityPlayer player = event.getEntityPlayer();
		WaterDrinkMessage result = this.tryDrink(player, event.getHand());
		if(result != null && !player.world.isRemote)
			HydrationModifier.spawnWaterDrinkParticles((WorldServer)player.world, result.getHitPosition());
	}
	
	/**
	 * Add handler for water bottles, so that drinking water
	 * from vanilla glass bottles adds hydration points as well.
	 * @param event The event
	 */
	@SubscribeEvent
	public void onItemConsumed(LivingEntityUseItemEvent.Finish event)
	{
		StatCapability.obtainRecord(this, event.getEntity()).ifPresent((SimpleStatRecord record) -> {
			ItemStack stack = event.getItem();

			// Water bottle
			if(stack.getItem() == Items.POTIONITEM)
			{
				PotionType potion = PotionUtils.getPotionFromItem(stack);
				if(potion == PotionTypes.WATER)
				{
					record.addToValue((float)ModConfig.HYDRATION.sipVolume * 2F);
				}
			}
		});
	}
	
	//=======================================================================
	//==========================/                 \==========================
	//==========================|UTILITY FUNCTIONS|==========================
	//==========================\_________________/==========================
	//=======================================================================
	
	/**
	 * A utility function to check whether a certain {@link PlayerInteractEvent}
	 * is suitable to be interpreted as water drink event
	 * @param event The event in question
	 * @return True if the event can be interpreted as water drinking, false otherwise
	 */
	private static boolean isDrinkEventInvalid(PlayerInteractEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		return event.getHand() != EnumHand.MAIN_HAND || (player.isCreative() || player.isSpectator());
	}

	/**
	 * Checks whether the player is looking at a block of water, and if so, evaluates
	 * the water block for quality and applies the resultant {@link WaterVolume}
	 * to the player's hydration record with the associated effects.
	 * @param player The player in question
	 * @param hand The currently evaluated hand
	 * @return WaterDrinkMessage summarizing the results, null if there is no water
	 */
	@Nullable
	private WaterDrinkMessage tryDrink(EntityPlayer player, EnumHand hand)
	{
		RayTraceResult water_rt = HydrationModifier.raytraceWaterDrinking(player, hand);
		if(water_rt != null)
		{
			SimpleStatRecord hydration = StatCapability.obtainRecord(this, player).orElse(null);
			if(hydration == null)
				return null;

			WaterVolume volume = WaterVolume.fromBlock(player.world, water_rt.getBlockPos(), (float)ModConfig.HYDRATION.sipVolume);

			// Test whether WaterVolume#fromBlock returned actual WaterVolume instance
			if(volume == null)
				return null;

			volume.consume(player);
			return new WaterDrinkMessage(volume, water_rt, hand);
		}
		return null;
	}
	
	/**
	 * Returns the raytrace result if the water drinking conditions were met, or null otherwise.
	 * @param player The player to do the raytrace from
	 * @param hand The hand used during the raytrace
	 * @return RayTraceResult if the drink conditions are met, null otherwise
	 */
	@Nullable
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
	
	/**
	 * A utility function to spawn particles representing tiny water splashes.
	 * @param world The server world
	 * @param position The position to spawn the particles around
	 */
	private static void spawnWaterDrinkParticles(WorldServer world, Vec3d position)
	{
		world.spawnParticle(EnumParticleTypes.WATER_SPLASH, position.x, position.y + 0.1, position.z, 20, 0.5, 0.1, 0.5, 0.1);
		world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, position.x, position.y + 0.1, position.z, 20, 0.5, 0.1, 0.5, 0.1);
		world.playSound(null, new BlockPos(position), SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.AMBIENT, 0.5f, 1.5f);
	}
}
