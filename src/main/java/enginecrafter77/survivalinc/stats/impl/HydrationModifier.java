package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.WaterDrinkMessage;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.DamageStatEffect;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
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
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HydrationModifier implements IMessageHandler<WaterDrinkMessage, IMessage>, StatProvider<SimpleStatRecord> {
	private static final long serialVersionUID = 6252973395407389818L;

	public static final FunctionalEffectFilter<Object> isOutsideOverworld = FunctionalEffectFilter.byPlayer((EntityPlayer player) -> player.dimension != 0);
	public static final DamageSource DEHYDRATION = new DamageSource("survivalinc_dehydration").setDamageIsAbsolute().setDamageBypassesArmor();
	public static final HydrationModifier instance = new HydrationModifier();
	
	public final Map<Item, Float> foodHydrationMap;
	
	public final EffectApplicator<SimpleStatRecord> effects;
	
	public HydrationModifier()
	{
		this.effects = new EffectApplicator<SimpleStatRecord>();
		this.foodHydrationMap = new HashMap<Item, Float>();
	}
	
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(HydrationModifier.class);
		FunctionalEffectFilter<SimpleStatRecord> nasfat = FunctionalEffectFilter.byValue(Range.lessThan(15F));
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.006F)).addFilter(HydrationModifier.isOutsideOverworld);
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.5F)).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isInLava));
		this.effects.add(new DamageStatEffect(HydrationModifier.DEHYDRATION, 4F, 0)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(5F)));
		this.effects.add(new PotionStatEffect(MobEffects.SLOWNESS, 5)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(40F)));
		this.effects.add(new PotionStatEffect(MobEffects.WEAKNESS, 5)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(40F)));
		this.effects.add(new PotionStatEffect(MobEffects.MINING_FATIGUE, 5)).addFilter(nasfat);
		this.effects.add(new PotionStatEffect(MobEffects.NAUSEA, 5)).addFilter(nasfat);
		this.effects.add(HydrationModifier::naturalDrain);
	}
	
	public void buildCompatMaps()
	{
		// Compile food value list
		for(String entry : ModConfig.HYDRATION.foodHydrationMap)
		{
			int separator = entry.lastIndexOf(' ');
			Item target = Item.getByNameOrId(entry.substring(0, separator));
			Float value = Float.parseFloat(entry.substring(separator + 1));
			this.foodHydrationMap.put(target, value);
		}
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
		SimpleStatRecord record = new SimpleStatRecord();
		record.setValueRange(Range.closed(0F, 100F));
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
	@Override
	public IMessage onMessage(WaterDrinkMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		WorldServer world = player.getServerWorld();
		
		RayTraceResult water_rt = null;
		
		// Verify if the client message is not fake (i.e. to artificially increase hydration)
		if(ModConfig.GENERAL.verifyClientDrinkingRequests)
		{
			water_rt = raytraceWaterDrinking(player, message.getHand());
			if(water_rt == null)
			{
				SurvivalInc.logger.warn("Player {}'s client probably issued a forged WaterDrinkMessage.");
				return null;
			}
			else HydrationModifier.spawnWaterDrinkParticles(world, water_rt.hitVec);
		}
		
		SurvivalInc.logger.info("Player drink request authorized.");
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		SimpleStatRecord hydration = tracker.getRecord(HydrationModifier.instance);
		hydration.addToValue((float)ModConfig.HYDRATION.sipVolume);
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
			SimpleStatRecord hydration = tracker.getRecord(HydrationModifier.instance);
			hydration.addToValue((float)ModConfig.HYDRATION.sipVolume);
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
			SimpleStatRecord hydration = tracker.getRecord(HydrationModifier.instance);
			hydration.addToValue((float)ModConfig.HYDRATION.sipVolume);
			if(!player.world.isRemote) HydrationModifier.spawnWaterDrinkParticles((WorldServer)player.world, water_rt.hitVec);
		}
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
		
		Float value = HydrationModifier.instance.foodHydrationMap.get(stack.getItem());
		if(value != null)
		{
			record.addToValue(value);
			return;
		}
		
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
