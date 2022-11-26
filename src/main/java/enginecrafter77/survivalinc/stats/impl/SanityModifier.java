package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.ClientProxy;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.AbsoluteElementLayoutFunction;
import enginecrafter77.survivalinc.client.HUDConstructEvent;
import enginecrafter77.survivalinc.client.StatFillBar;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.stats.*;
import enginecrafter77.survivalinc.stats.effect.*;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Rectangle;

import java.util.List;

public class SanityModifier implements StatProvider<SanityRecord> {
	public static final ResourceLocation DISTORT_SHADER = new ResourceLocation(SurvivalInc.MOD_ID, "shaders/distort.json");
	public static final SoundEvent STATIC_BUZZ = new SoundEvent(new ResourceLocation(SurvivalInc.MOD_ID, "staticbuzz"));
	
	public final EffectApplicator<SanityRecord> effects;
	
	public SanityModifier()
	{
		this.effects = new EffectApplicator<SanityRecord>();
		
		if(ModConfig.WETNESS.enabled) this.effects.add(this::whenWet).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isInWater).invert());
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, 0.004F)).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isPlayerSleeping));
		this.effects.add(this::whenInDark).addFilter(HydrationModifier.IS_OUTSIDE_OVERWORLD.invert());
		this.effects.add(this::whenNearEntities);
		this.effects.add(this::sleepDeprivation);
		
		if(ModConfig.SANITY.staticBuzzIntensity > 0D)
		{
			this.effects.add(this::sanityScreenDistortion).addFilter(SideEffectFilter.CLIENT);
		}
		else
		{
			this.effects.add(new PotionStatEffect(MobEffects.NAUSEA, 2).setDuration(200).setResetThreshold(0.5F)).addFilter(this::isSanityBeyondThreshold);
		}
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
	public void registerStat(StatRegisterEvent event)
	{
		event.register(this);
	}

	@SubscribeEvent
	public void constructHud(HUDConstructEvent event)
	{
		StatFillBar<SanityRecord> bar = new StatFillBar<SanityRecord>(this, ModConfig.CLIENT.hud.sanityBarDirection, ClientProxy.STAT_ICONS.region(new Rectangle(0, 0, 9, 9)));
		bar.addLayer(ClientProxy.STAT_ICONS.region(new Rectangle(9, 0, 9, 9)), SimpleStatRecord::getNormalizedValue);
		bar.setCapacity(ModConfig.CLIENT.hud.sanityBarCapacity);
		bar.setSpacing(ModConfig.CLIENT.hud.sanityBarSpacing);

		if(ModConfig.CLIENT.hud.stackSanityBar)
			event.addElement(bar, ModConfig.CLIENT.hud.sanityBarStack).setTrigger(ModConfig.CLIENT.hud.sanityBarRenderTrigger).addFilter(ClientProxy.TEXTURE_RESET_FILTER);
		else
			event.addElement(bar, new AbsoluteElementLayoutFunction((float)ModConfig.CLIENT.hud.originX, (float)ModConfig.CLIENT.hud.originY, ModConfig.CLIENT.hud.sanityBarX, ModConfig.CLIENT.hud.sanityBarY));
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public void sleepDeprivation(SanityRecord record, EntityPlayer player)
	{
		if(record.getTicksAwake() > ModConfig.SANITY.sleepDeprivationMin)
		{
			record.addToValue(-(float)ModConfig.SANITY.sleepDeprivationDebuff * (record.getTicksAwake() - ModConfig.SANITY.sleepDeprivationMin) / (ModConfig.SANITY.sleepDeprivationMax - ModConfig.SANITY.sleepDeprivationMin));
		}
	}
	
	@FunctionalImplementation(of = EffectFilter.class)
	public boolean isSanityBeyondThreshold(SanityRecord record, EntityPlayer player)
	{
		return record.getValue() < ((float)ModConfig.SANITY.hallucinationThreshold * SanityRecord.values.upperEndpoint());
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public void sanityScreenDistortion(SanityRecord record, EntityPlayer player)
	{
		float threshold = (float)ModConfig.SANITY.hallucinationThreshold * SanityRecord.values.upperEndpoint();
		if(player.world.getWorldTime() % 160 == 0)
		{
			/*
			 * This effect should only apply to the client player. Other player entities on client-side world should not be
			 * evaluated.
			 */
			Minecraft client = Minecraft.getMinecraft();
			if(player != client.player) return;
			
			if(player.world.rand.nextFloat() < 0.25F && record.getValue() < threshold)
			{
				// 1F - current / threshold => this calculation is used to increase the volume for "more insane" players, up to 100% original volume (applied at sanity 0)
				float volume = (1F - record.getValue() / threshold) * (float)ModConfig.SANITY.staticBuzzIntensity;
				player.world.playSound(player.posX, player.posY, player.posZ, SanityModifier.STATIC_BUZZ, SoundCategory.AMBIENT, volume, 1, false);
				client.entityRenderer.loadShader(SanityModifier.DISTORT_SHADER);
			}
			else
			{
				// Check if the current shader is our shader, and if so, stop using it.
				ShaderGroup shader = client.entityRenderer.getShaderGroup();
				if(shader != null && shader.getShaderGroupName().equals(SanityModifier.DISTORT_SHADER.toString()))
				{
					client.entityRenderer.stopUseShader();
				}
			}
		}
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public void whenInDark(SanityRecord record, EntityPlayer player)
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
	public void whenWet(SanityRecord record, EntityPlayer player)
	{
		StatCapability.obtainRecord(SurvivalInc.wetness, player).ifPresent((SimpleStatRecord wetness) -> {
			float boundary = (float)ModConfig.SANITY.wetnessAnnoyanceThreshold;
			if(wetness.getNormalizedValue() > boundary)
			{
				record.addToValue(((wetness.getNormalizedValue() - boundary) / (1F - boundary)) * -(float)ModConfig.SANITY.maxWetnessAnnoyance);
			}
		});
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public void whenNearEntities(SanityRecord record, EntityPlayer player)
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
	public void onClientWorldTick(TickEvent.PlayerTickEvent event)
	{
		if(event.side.isServer()) return;
		
		Minecraft client = Minecraft.getMinecraft();
		ShaderGroup shader = client.entityRenderer.getShaderGroup();
		StatTracker tracker = event.player.getCapability(StatCapability.target, null);

		if(tracker == null)
			return;

		if(event.player.world.getWorldTime() % 160 == 0 && shader != null && shader.getShaderGroupName().equals(SanityModifier.DISTORT_SHADER.toString()) && !tracker.isActive(this, null))
			client.entityRenderer.stopUseShader();
	}
	
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		event.getRegistry().register(SanityModifier.STATIC_BUZZ);
	}
	
	@SubscribeEvent
	public void onPlayerWakeUp(PlayerWakeUpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(event.shouldSetSpawn()) // If the "lying in bed" was successful (the player actually fell asleep)
		{
			StatCapability.obtainRecord(this, player).ifPresent((SanityRecord record) -> {
				record.addToValue(record.getValueRange().upperEndpoint() * (float)ModConfig.SANITY.sleepResoration);
				record.resetSleep();
				StatCapability.synchronizeStats(StatSyncMessage.withPlayer(player));
				player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() - 8);
			});
		}
	}
	
	@SubscribeEvent
	public void onTame(AnimalTameEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer)
			StatCapability.obtainRecord(this, (EntityPlayer)ent).ifPresent((SanityRecord record) -> record.addToValue((float)ModConfig.SANITY.animalTameBoost));
	}
}
