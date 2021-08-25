package enginecrafter77.survivalinc.stats.impl;

import javax.annotation.Nullable;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.WaterDrinkMessage;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.DamageStatEffect;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
import enginecrafter77.survivalinc.stats.effect.EffectFilter;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.PotionStatEffect;
import enginecrafter77.survivalinc.stats.effect.ValueStatEffect;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HydrationModifier implements StatProvider<SimpleStatRecord> {
	private static final long serialVersionUID = 6252973395407389818L;

	public static final EffectFilter<StatRecord> isOutsideOverworld = FunctionalEffectFilter.byPlayer((EntityPlayer player) -> player.dimension != 0);
	public static final DamageSource DEHYDRATION = new DamageSource("survivalinc_dehydration").setDamageIsAbsolute().setDamageBypassesArmor();
	public static HydrationModifier instance = null;
	
	public final EffectApplicator<SimpleStatRecord> effects;
	
	private HydrationModifier()
	{
		this.effects = new EffectApplicator<SimpleStatRecord>();
		
		EffectFilter<SimpleStatRecord> fatique = FunctionalEffectFilter.byValue(Range.lessThan(10F));
		EffectFilter<SimpleStatRecord> slowness = FunctionalEffectFilter.byValue(Range.lessThan(20F));
		
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.006F)).addFilter(HydrationModifier.isOutsideOverworld);
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.5F)).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isInLava));
		this.effects.add(new DamageStatEffect(HydrationModifier.DEHYDRATION, 4F, 0)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(5F)));
		this.effects.add(new PotionStatEffect(MobEffects.SLOWNESS, 5)).addFilter(slowness);
		this.effects.add(new PotionStatEffect(MobEffects.WEAKNESS, 5)).addFilter(slowness);
		this.effects.add(new PotionStatEffect(MobEffects.MINING_FATIGUE, 5)).addFilter(fatique);
		this.effects.add(new PotionStatEffect(MobEffects.NAUSEA, 5)).addFilter(fatique);
		this.effects.add(HydrationModifier::naturalDrain);
	}
	
	public static void init()
	{
		HydrationModifier.instance = new HydrationModifier();
		MinecraftForge.EVENT_BUS.register(HydrationModifier.class);
	}
	
	/**
	 * A simple method to check whether the provider was loaded or not.
	 * This should coincide with whether the provider is registered in
	 * the player's stat registry. This should NOT be confused with {@link enginecrafter77.survivalinc.config.HydrationConfig#enabled},
	 * since the latter can be changed during the game.
	 * @return True if the {@link #init()} method has been called in the past, false otherwise.
	 */
	public static boolean loaded()
	{
		return HydrationModifier.instance != null;
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
		SimpleStatRecord record = new SimpleStatRecord(Range.closed(0F, 100F));
		record.setValue((float)ModConfig.HYDRATION.startValue);
		return record;
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
		
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		SimpleStatRecord hydration = tracker.getRecord(HydrationModifier.instance);
		HydrationModifier.spawnWaterDrinkParticles(world, hitvec);
		volume.apply(hydration, player);
		return null;
	}
	
	public static void naturalDrain(SimpleStatRecord record, EntityPlayer player)
	{
		float drain = (float)ModConfig.HYDRATION.passiveDrain;
		if(ModConfig.HEAT.enabled)
		{
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			SimpleStatRecord heat = tracker.getRecord(HydrationModifier.instance);
			if(heat.getNormalizedValue() > ModConfig.HYDRATION.sweatingThreshold)
				drain *= ModConfig.HYDRATION.sweatingMultiplier;
		}
		record.addToValue(-drain);
	}
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(HydrationModifier.instance);
	}
	
	/**
	 * This method gets run only on client. It basically sends "water drink request"
	 * to the server. This approach was chosen since this method is run only on client,
	 * and since the client can easily be modified to artificially create these packets,
	 * it is therefore always validated on the server side.
	 * 
	 * Essentially, this method differs from {@link #onPlayerInteract(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock)}
	 * by only that when the player click on water without reading for a block underneath it, then
	 * this event is issued since it's treated like the player clicked with empty hand in the air.
	 * @param event The empty hand interaction event
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onPlayerRightClick(PlayerInteractEvent.RightClickEmpty event)
	{
		if(!HydrationModifier.validateDrinkEvent(event)) return;
		
		WaterDrinkMessage result = HydrationModifier.tryDrink(event.getEntityPlayer(), event.getHand());
		if(result != null) SurvivalInc.proxy.net.sendToServer(result);
	}
	
	/**
	 * This event gets fired on both sides, so it's not necessary to get
	 * server authorization regarding the drinking.
	 * @param event The block click event
	 */
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
	{
		if(!HydrationModifier.validateDrinkEvent(event)) return;
		
		EntityPlayer player = event.getEntityPlayer();
		WaterDrinkMessage result = HydrationModifier.tryDrink(player, event.getHand());
		if(result != null && !player.world.isRemote)
			HydrationModifier.spawnWaterDrinkParticles((WorldServer)player.world, result.getHitPosition());
	}
	
	/**
	 * Add handler for water bottles, so that drinking water
	 * from vanilla glass bottles adds hydration points as well.
	 * @param event The event
	 */
	@SubscribeEvent
	public static void onItemConsumed(LivingEntityUseItemEvent.Finish event)
	{
		StatTracker stats = event.getEntityLiving().getCapability(StatCapability.target, null);		
		SimpleStatRecord record = stats.getRecord(HydrationModifier.instance);
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
	}
	
	//=======================================================================
	//==========================/                 \==========================
	//==========================|UTILITY FUNCTIONS|==========================
	//==========================\_________________/==========================
	//=======================================================================
	
	/**
	 * An utility function to check whether a certain {@link PlayerInteractEvent}
	 * is suitable to be interpreted as water drink event
	 * @param event The event in question
	 * @return True if the event can be interpreted as water drinking, false otherwise
	 */
	private static boolean validateDrinkEvent(PlayerInteractEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		return event.getHand() == EnumHand.MAIN_HAND && !(player.isCreative() || player.isSpectator());
	}
	
	/**
	 * Checks whether the player is looking at a block of water, and if so, evaluates
	 * the water block for quality and applies the resultant {@link WaterVolume}
	 * to the player's hydration record with the associated effects.
	 * @param player The player in question
	 * @param hand The currently evaluated hand
	 * @return WaterDrinkMessage summarizing the results, null if there is no water
	 */
	private static @Nullable WaterDrinkMessage tryDrink(EntityPlayer player, EnumHand hand)
	{
		RayTraceResult water_rt = HydrationModifier.raytraceWaterDrinking(player, hand);
		if(water_rt != null)
		{
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			SimpleStatRecord hydration = tracker.getRecord(HydrationModifier.instance);
			WaterVolume volume = WaterVolume.fromBlock(player.world, water_rt.getBlockPos(), (float)ModConfig.HYDRATION.sipVolume);
			
			// Test whether WaterVolume#fromBlock returned actual WaterVolume instance
			if(volume == null) return null;
			
			volume.apply(hydration, player);
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
	private static @Nullable RayTraceResult raytraceWaterDrinking(EntityPlayer player, EnumHand hand)
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
	 * An utility function to spawn particles representing tiny water splashes.
	 * @param world The server world
	 * @param position The position to spawn the particles around
	 */
	private static void spawnWaterDrinkParticles(WorldServer world, Vec3d position)
	{
		world.spawnParticle(EnumParticleTypes.WATER_SPLASH, position.x, position.y + 0.1, position.z, 20, 0.5, 0.1, 0.5, 0.1, null);
		world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, position.x, position.y + 0.1, position.z, 20, 0.5, 0.1, 0.5, 0.1, null);
		world.playSound(null, new BlockPos(position), SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.AMBIENT, 0.5f, 1.5f);
	}
}
