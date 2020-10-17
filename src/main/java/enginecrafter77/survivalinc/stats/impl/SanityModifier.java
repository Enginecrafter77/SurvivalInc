package enginecrafter77.survivalinc.stats.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.SideEffectFilter;
import enginecrafter77.survivalinc.stats.effect.ValueStatEffect;

public class SanityModifier implements StatProvider {
	private static final long serialVersionUID = 6707924203617912749L;
	
	public static final ResourceLocation distortshader = new ResourceLocation(SurvivalInc.MOD_ID, "shaders/distort.json");
	public static final SoundEvent staticbuzz = new SoundEvent(new ResourceLocation(SurvivalInc.MOD_ID, "staticbuzz"));
	public static final SanityModifier instance = new SanityModifier();
	
	public final EffectApplicator<SanityRecord> effects;
	public final Map<Item, Float> foodSanityMap;
	
	public SanityModifier()
	{
		this.effects = new EffectApplicator<SanityRecord>();
		this.foodSanityMap = new HashMap<Item, Float>();
	}
	
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(SanityModifier.class);
		if(ModConfig.WETNESS.enabled) this.effects.add(SanityModifier::whenWet);
		this.effects.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, 0.004F)).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isPlayerSleeping));
		this.effects.add(SanityModifier::whenInDark).addFilter(HydrationModifier.isOutsideOverworld.invert());
		this.effects.add(SanityModifier::playStaticNoise).addFilter(SideEffectFilter.CLIENT);
		this.effects.add(SanityModifier::whenNearEntities);
		this.effects.add(SanityModifier::sleepDeprivation);
		
		// Compile food value list
		for(String entry : ModConfig.SANITY.foodSanityMap)
		{
			int separator = entry.lastIndexOf(' ');
			Item target = Item.getByNameOrId(entry.substring(0, separator));
			Float value = Float.parseFloat(entry.substring(separator + 1));
			this.foodSanityMap.put(target, value);
		}
	}
	
	@Override
	public void update(EntityPlayer target, StatRecord record)
	{
		if(target.isCreative() || target.isSpectator()) return;
		
		SanityRecord sanity = (SanityRecord)record;
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
	public StatRecord createNewRecord()
	{
		return new SanityRecord();
	}
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(SanityModifier.instance);
	}
	
	public static void sleepDeprivation(SanityRecord record, EntityPlayer player)
	{		
		if(record.getTicksAwake() > ModConfig.SANITY.sleepDeprivationMin)
		{
			record.addToValue(-(float)ModConfig.SANITY.sleepDeprivationDebuff * (record.getTicksAwake() - ModConfig.SANITY.sleepDeprivationMin) / (ModConfig.SANITY.sleepDeprivationMax - ModConfig.SANITY.sleepDeprivationMin));
		}
	}
	
	public static void playStaticNoise(SanityRecord record, EntityPlayer player)
	{
		float threshold = (float)ModConfig.SANITY.hallucinationThreshold * SanityRecord.values.upperEndpoint();
		if(player.world.getWorldTime() % 160 == 0)
		{
			/**
			 * This effect should only apply to the client player.
			 * Other player entities on client-side world should
			 * not be evaluated.
			 */
			Minecraft client = Minecraft.getMinecraft();
			if(player != client.player) return;
			
			if(player.world.rand.nextFloat() < 0.25F && record.getValue() < threshold)
			{
				// 1F - current / threshold => this calculation is used to increase the volume for "more insane" players, up to 100% original volume (applied at sanity 0)
				float volume = (1F - record.getValue() / threshold) * (float)ModConfig.SANITY.staticBuzzIntensity;
				player.world.playSound(player.posX, player.posY, player.posZ, staticbuzz, SoundCategory.AMBIENT, volume, 1, false);
				client.entityRenderer.loadShader(distortshader);
			}
			else
			{
				// Check if the current shader is our shader, and if so, stop using it.
				ShaderGroup shader = client.entityRenderer.getShaderGroup();
				if(shader != null && shader.getShaderGroupName().equals(distortshader.toString()))
				{
					client.entityRenderer.stopUseShader();
				}
			}
		}
	}
	
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
	
	public static void whenWet(SanityRecord record, EntityPlayer player)
	{
		float boundary = (float)ModConfig.SANITY.wetnessAnnoyanceThreshold;
		StatTracker stats = player.getCapability(StatCapability.target, null);
		SimpleStatRecord wetness = (SimpleStatRecord)stats.getRecord(SanityModifier.instance);
		float annoyance = (wetness.getValue() - wetness.valuerange.upperEndpoint()) / 10000;
		annoyance = (boundary / -10000) - annoyance;
		
		if(wetness.getValue() >= boundary) record.addToValue(-annoyance);
	}
	
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
			else if(creature instanceof EntityMob)
				value -= ModConfig.SANITY.hostileMobModifier;
		}
		record.setValue(value);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientWorldTick(TickEvent.ClientTickEvent event)
	{
		Minecraft client = Minecraft.getMinecraft();
		ShaderGroup shader = client.entityRenderer.getShaderGroup();
		if(shader != null && client.world.getWorldTime() % 160 == 0 && (client.player.isCreative() || client.player.isSpectator()) && shader.getShaderGroupName().equals(distortshader.toString()))
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
			SanityRecord sanity = (SanityRecord)stats.getRecord(SanityModifier.instance);
			sanity.addToValue(sanity.valuerange.upperEndpoint() * (float)ModConfig.SANITY.sleepResoration);
			sanity.resetSleep();
			SurvivalInc.proxy.net.sendToAll(new StatSyncMessage(player));
			player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() - 8);
		}
	}
	
	@SubscribeEvent
	public static void onConsumeItem(LivingEntityUseItemEvent.Finish event)
	{		
		Entity ent = event.getEntity();	
		if(ent instanceof EntityPlayer)
		{
			try
			{
				// Try to get the modifier from the map (throws NPE when no such mapping exists)
				float mod = SanityModifier.instance.foodSanityMap.get(event.getItem().getItem());
				
				// Modify the sanity value
				EntityPlayer player = (EntityPlayer)ent;
				StatTracker stats = player.getCapability(StatCapability.target, null);
				SimpleStatRecord sanity = (SimpleStatRecord)stats.getRecord(SanityModifier.instance);
				sanity.addToValue(mod);
			}
			catch(NullPointerException exc)
			{
				// Food simply doesn't have any sanity mapping associated
			}
		}
	}
	
	@SubscribeEvent
	public static void onTame(AnimalTameEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer)
		{
			StatTracker stat = ent.getCapability(StatCapability.target, null);
			SimpleStatRecord sanity = (SimpleStatRecord)stat.getRecord(SanityModifier.instance);
			sanity.addToValue((float)ModConfig.SANITY.animalTameBoost);
		}
	}
	
	public static class SanityRecord extends SimpleStatRecord
	{
		public static final Range<Float> values = Range.closed(0F, 100F);
		
		protected int ticksAwake;
		
		public SanityRecord()
		{
			super(values);
			this.setValue((float)ModConfig.SANITY.startValue);
			this.ticksAwake = 0;
		}
		
		public void resetSleep()
		{
			this.ticksAwake = 0;
		}
		
		public int getTicksAwake()
		{
			return this.ticksAwake;
		}

		@Override
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound tag = super.serializeNBT();
			tag.setInteger("ticksAwake", this.ticksAwake);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			super.deserializeNBT(nbt);
			this.ticksAwake = nbt.getInteger("ticksAwake");
		}
		
		@Override
		public String toString()
		{
			return super.toString() + String.format(" [SD: %d]", this.ticksAwake);
		}
	}
}