package enginecrafter77.survivalinc.stats.impl;

import java.util.List;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
import enginecrafter77.survivalinc.stats.effect.EffectFilter;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.PotionStatEffect;
import enginecrafter77.survivalinc.stats.effect.SideEffectFilter;
import enginecrafter77.survivalinc.stats.effect.StatEffect;
import enginecrafter77.survivalinc.stats.effect.ValueStatEffect;
import enginecrafter77.survivalinc.util.FunctionalImplementation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SanityModifier implements StatProvider<SanityRecord> {
	private static final long serialVersionUID = 6707924203617912749L;
	
	public static final ResourceLocation distortshader = new ResourceLocation(SurvivalInc.MOD_ID, "shaders/distort.json");
	public static final SoundEvent staticbuzz = new SoundEvent(new ResourceLocation(SurvivalInc.MOD_ID, "staticbuzz"));
	public static SanityModifier instance = null;
	
	public final EffectApplicator<SanityRecord> effects;
	
	public SanityModifier()
	{
		this.effects = new EffectApplicator<SanityRecord>();
		
		if(ModConfig.WETNESS.enabled) this.effects.add(SanityModifier::whenWet).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isInWater).invert());
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, 0.004F)).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isPlayerSleeping));
		this.effects.add(SanityModifier::whenInDark).addFilter(HydrationModifier.isOutsideOverworld.invert());
		this.effects.add(SanityModifier::whenNearEntities);
		this.effects.add(SanityModifier::sleepDeprivation);
		
		if(ModConfig.SANITY.staticBuzzIntensity > 0D)
		{
			this.effects.add(SanityModifier::sanityScreenDistortion).addFilter(SideEffectFilter.CLIENT);
		}
		else
		{
			this.effects.add(new PotionStatEffect(MobEffects.NAUSEA, 2).setDuration(200).setResetThreshold(0.5F)).addFilter(SanityModifier::isSanityBeyondThreshold);
		}
	}
	
	public static void init()
	{
		SanityModifier.instance = new SanityModifier();
		MinecraftForge.EVENT_BUS.register(SanityModifier.class);
	}
	
	/**
	 * A simple method to check whether the provider was loaded or not. This should coincide with whether the provider is
	 * registered in the player's stat registry. This should NOT be confused with
	 * {@link enginecrafter77.survivalinc.config.SanityConfig#enabled}, since the latter can be changed during the game.
	 * @return True if the {@link #init()} method has been called in the past, false otherwise.
	 */
	public static boolean loaded()
	{
		return SanityModifier.instance != null;
	}
	
	@Override
	public void update(EntityPlayer target, SanityRecord sanity)
	{
		if(target.isCreative() || target.isSpectator()) return;
		
		++sanity.ticksAwake;
		this.effects.apply(sanity, target);
		sanity.checkoutValueChange();
	}
	
	@Override
	public ResourceLocation getStatID()
	{
		return new ResourceLocation(SurvivalInc.MOD_ID, "sanity");
	}
	
	@Override
	public SanityRecord createNewRecord()
	{
		return new SanityRecord();
	}
	
	@Override
	public Class<SanityRecord> getRecordClass()
	{
		return SanityRecord.class;
	}
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(SanityModifier.instance);
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public static void sleepDeprivation(SanityRecord record, EntityPlayer player)
	{
		if(record.getTicksAwake() > ModConfig.SANITY.sleepDeprivationMin)
		{
			record.addToValue(-(float)ModConfig.SANITY.sleepDeprivationDebuff * (record.getTicksAwake() - ModConfig.SANITY.sleepDeprivationMin) / (ModConfig.SANITY.sleepDeprivationMax - ModConfig.SANITY.sleepDeprivationMin));
		}
	}
	
	@FunctionalImplementation(of = EffectFilter.class)
	public static boolean isSanityBeyondThreshold(SanityRecord record, EntityPlayer player)
	{
		return record.getValue() < ((float)ModConfig.SANITY.hallucinationThreshold * SanityRecord.values.upperEndpoint());
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public static void sanityScreenDistortion(SanityRecord record, EntityPlayer player)
	{
		float threshold = (float)ModConfig.SANITY.hallucinationThreshold * SanityRecord.values.upperEndpoint();
		if(player.world.getWorldTime() % 160 == 0)
		{
			/**
			 * This effect should only apply to the client player. Other player entities on client-side world should not be
			 * evaluated.
			 */
			Minecraft client = Minecraft.getMinecraft();
			if(player != client.player) return;
			
			if(player.world.rand.nextFloat() < 0.25F && record.getValue() < threshold)
			{
				// 1F - current / threshold => this calculation is used to increase the volume for "more insane" players, up to 100% original volume (applied at sanity 0)
				float volume = (1F - record.getValue() / threshold) * (float)ModConfig.SANITY.staticBuzzIntensity;
				player.world.playSound(player.posX, player.posY, player.posZ, SanityModifier.staticbuzz, SoundCategory.AMBIENT, volume, 1, false);
				client.entityRenderer.loadShader(SanityModifier.distortshader);
			}
			else
			{
				// Check if the current shader is our shader, and if so, stop using it.
				ShaderGroup shader = client.entityRenderer.getShaderGroup();
				if(shader != null && shader.getShaderGroupName().equals(SanityModifier.distortshader.toString()))
				{
					client.entityRenderer.stopUseShader();
				}
			}
		}
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public static void whenInDark(SanityRecord record, EntityPlayer player)
	{
		BlockPos position = new BlockPos(player.getPositionVector().add(0D, player.getEyeHeight(), 0D));
		int lightlevel = player.world.getLight(position);
		
		// If there is not enough light, steve/alex feels anxious
		if(lightlevel < ModConfig.SANITY.comfortLightLevel)
		{
			float darknesslevel = (float)(ModConfig.SANITY.comfortLightLevel - lightlevel) / (float)ModConfig.SANITY.comfortLightLevel;
			record.addToValue((float)ModConfig.SANITY.darkSpookFactorBase * -darknesslevel);
		}
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public static void whenWet(SanityRecord record, EntityPlayer player)
	{
		float boundary = (float)ModConfig.SANITY.wetnessAnnoyanceThreshold;
		StatTracker stats = player.getCapability(StatCapability.target, null);
		SimpleStatRecord wetness = stats.getRecord(WetnessModifier.instance);
		if(wetness.getNormalizedValue() > boundary)
		{
			record.addToValue(((wetness.getNormalizedValue() - boundary) / (1F - boundary)) * -(float)ModConfig.SANITY.maxWetnessAnnoyance);
		}
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public static void whenNearEntities(SanityRecord record, EntityPlayer player)
	{
		BlockPos origin = player.getPosition();
		Vec3i offset = new Vec3i(3, 3, 3);
		
		AxisAlignedBB box = new AxisAlignedBB(origin.subtract(offset), origin.add(offset));
		List<EntityCreature> entities = player.world.getEntitiesWithinAABB(EntityCreature.class, box);
		
		float value = record.getValue();
		for(EntityCreature creature : entities)
		{
			if(creature instanceof EntityTameable)
			{
				EntityTameable pet = (EntityTameable)creature;
				// 4x bonus for tamed creatures. Having pets has it's perks :D
				float bonus = pet.isTamed() ? (float)ModConfig.SANITY.tamedMobMultiplier : 1;
				value += ModConfig.SANITY.friendlyMobBonus * bonus;
			}
			else if(creature instanceof EntityAnimal)
				value += ModConfig.SANITY.friendlyMobBonus;
			else if(creature instanceof EntityMob) value -= ModConfig.SANITY.hostileMobModifier;
		}
		record.setValue(value);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientWorldTick(TickEvent.PlayerTickEvent event)
	{
		if(event.side.isServer()) return;
		
		Minecraft client = Minecraft.getMinecraft();
		ShaderGroup shader = client.entityRenderer.getShaderGroup();
		StatTracker tracker = event.player.getCapability(StatCapability.target, null);
		if(shader != null && event.player.world.getWorldTime() % 160 == 0 && shader.getShaderGroupName().equals(SanityModifier.distortshader.toString()) && !tracker.isActive(SanityModifier.instance, null))
		{
			client.entityRenderer.stopUseShader();
		}
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		event.getRegistry().register(SanityModifier.staticbuzz);
	}
	
	@SubscribeEvent
	public static void onPlayerWakeUp(PlayerWakeUpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(event.shouldSetSpawn()) // If the "lying in bed" was successful (the player actually fell asleep)
		{
			StatTracker stats = player.getCapability(StatCapability.target, null);
			SanityRecord sanity = stats.getRecord(SanityModifier.instance);
			sanity.addToValue(sanity.getValueRange().upperEndpoint() * (float)ModConfig.SANITY.sleepResoration);
			sanity.resetSleep();
			SurvivalInc.proxy.net.sendToAll(new StatSyncMessage().addPlayer(player));
			player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() - 8);
		}
	}
	
	@SubscribeEvent
	public static void onTame(AnimalTameEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer)
		{
			StatTracker stat = ent.getCapability(StatCapability.target, null);
			SimpleStatRecord sanity = stat.getRecord(SanityModifier.instance);
			sanity.addToValue((float)ModConfig.SANITY.animalTameBoost);
		}
	}
}
