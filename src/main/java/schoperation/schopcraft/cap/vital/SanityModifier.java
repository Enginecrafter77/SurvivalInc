package schoperation.schopcraft.cap.vital;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import schoperation.schopcraft.CommonProxy;
import schoperation.schopcraft.cap.wetness.IWetness;
import schoperation.schopcraft.cap.wetness.WetnessProvider;
import schoperation.schopcraft.config.SchopConfig;
import schoperation.schopcraft.lib.ModItems;
import schoperation.schopcraft.packet.SummonInfoPacket;
import schoperation.schopcraft.util.SchopServerEffects;

import java.util.List;
import java.util.function.Predicate;

/*
 * Where sanity is modified.
 */

public class SanityModifier {

	// The first variable is used for the timer at the end of onPlayerUpdate to
	// allow for a hallucination once per 20 ticks.
	// The other is for spawning "Them".
	private int lucidTimer = 0;
	private int spawnThemTimer = 0;
	
	public static final Predicate<EntityPlayer> isOutsideOverworld = (EntityPlayer player) -> Math.abs(player.dimension) == 1;
	
	public static float whenInDark(EntityPlayer player)
	{
		// If we are not in overworld, skip this function
		if(isOutsideOverworld.test(player)) return 0F;
		int lightlevel = player.world.getLight(player.getPosition());
		// If there is enough light, steve/alex is happy
		if(lightlevel >= SchopConfig.MECHANICS.comfortLightLevel) return 0F;
		float darknesslevel = (float)(SchopConfig.MECHANICS.comfortLightLevel - lightlevel) / (float)SchopConfig.MECHANICS.comfortLightLevel;
		return (float)SchopConfig.MECHANICS.darkSpookFactorBase * -darknesslevel;
	}
	
	public static float whenWet(EntityPlayer player)
	{
		float boundary = (float)SchopConfig.MECHANICS.minAnnoyingWetness;
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		float annoyance = (wetness.getMaxWetness() - wetness.getWetness()) / 10000;
		annoyance = (boundary / -10000) - annoyance;
		return wetness.getWetness() < boundary ? 0F : annoyance;
	}
	
	public void onPlayerUpdate(Entity dummy)
	{
		// Capabilities
		VitalStat stats = dummy.getCapability(VitalStatProvider.VITAL_CAP, null);

		// ACTUAL position of player.
		double playerPosX = dummy.posX;
		double playerPosY = dummy.posY;
		double playerPosZ = dummy.posZ;
		
		// ===========================================================================
		// The Side Effects of Insanity
		// ===========================================================================

		// Every 20 ticks (1 second) there is a chance for a hallucination to
		// appear; visual, audial, or both.
		// In this case, a hallucination is a client-only particle/sound. The
		// "things" (Maxwell refers to them as "Them") are a different area.
		// The more insane the player is, the bigger the chance is.
		if (lucidTimer < 20)
		{

			// Increment timer until it reaches 20.
			lucidTimer++;
		}
		else
		{

			// Reset timer
			lucidTimer = 0;

			// Increment THIS timer
			spawnThemTimer++;

			// There'll only be hallucinations for players with less than 70% of
			// their sanity.
			if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.7))
			{

				// Determine if a hallucination should appear.
				double chanceOfHallucination = (double) (stats.getStat(VitalStatType.SANITY) / 100) + 0.3;
				double randomLucidNumber = Math.random();
				boolean shouldSpawnHallucination = chanceOfHallucination < randomLucidNumber;

				// So... should one appear?
				if (shouldSpawnHallucination)
				{

					// Now pick one... more random numbers!
					double pickAHallucination = Math.random();
					double randOffset = Math.random() * 6;
					int posOrNeg = (int) Math.round(Math.random());

					if (posOrNeg == 0)
					{
						randOffset = randOffset * -1;
					}

					// As of now... ten possibilities... all weighted equally.
					// These will be called on the client, so no one else can
					// see/hear them. Random positions nearby the player too.

					// Enderman noise + particles
					if (pickAHallucination >= 0 && pickAHallucination < 0.10)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"EndermanSound", "EndermanParticles", playerPosX + randOffset, playerPosY + 1,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}

					// Zombie sound
					else if (pickAHallucination >= 0.10 && pickAHallucination < 0.20)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"ZombieSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}

					// Ghast sound
					else if (pickAHallucination >= 0.20 && pickAHallucination < 0.30)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"GhastSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}

					// Explosion sound + particles
					else if (pickAHallucination >= 0.30 && pickAHallucination < 0.40)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"ExplosionSound", "ExplosionParticles", playerPosX + randOffset,
								playerPosY + randOffset, playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}

					// Stone sound
					else if (pickAHallucination >= 0.40 && pickAHallucination < 0.50)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"StoneBreakSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}

					// Mist in the air... tf???????
					else if (pickAHallucination >= 0.50 && pickAHallucination < 0.60)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"null", "CreepyMistParticles", playerPosX + randOffset, playerPosY + 1,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}

					// A guardian appearing in your face. This one still scares
					// the crap out of me.
					else if (pickAHallucination >= 0.60 && pickAHallucination < 0.70)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"null", "GuardianParticles", playerPosX, playerPosY, playerPosZ);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}

					// Fire sounds + smoke particles
					else if (pickAHallucination >= 0.70 && pickAHallucination < 0.80)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"FireSound", "SmokeParticles", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}

					// A�villager sound... are they lost?
					else if (pickAHallucination >= 0.80 && pickAHallucination < 0.90)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"VillagerSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}

					// Lava sound
					else if (pickAHallucination >= 0.90 && pickAHallucination <= 1.00)
					{

						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
								"LavaSound", "null", playerPosX + randOffset, playerPosY + randOffset,
								playerPosZ + randOffset);
						CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
					}
				}
			}

			// There are other side effects of insanity other than
			// hallucinations.
			// Here, the player's view is wobbled/distorted
			// Some weird ambience is added to make insanity feel more insane.
			// And... weird. It's just the right word.
			// Also, They may come and attack you.

			// Make the screen of the insane player wobble.
			if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.35))
			{

				SchopServerEffects.affectPlayer(dummy.getCachedUniqueIdString(), "nausea", 100, 5, false, false);
			}

			// Add some weird insanity ambiance.
			if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.20))
			{

				// Random chance so it doesn't overlap with itself.
				double randInsanityAmbience = Math.random();

				if (randInsanityAmbience < 0.20)
				{

					IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
							"InsanityAmbienceSoundLoud", "null", playerPosX, playerPosY, playerPosZ);
					CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
				}
			}
			else if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.50))
			{

				// Random chance so it doesn't overlap with itself.
				double randInsanityAmbience = Math.random();

				if (randInsanityAmbience < 0.20)
				{

					IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(dummy.getCachedUniqueIdString(),
							"InsanityAmbienceSound", "null", playerPosX, playerPosY, playerPosZ);
					CommonProxy.net.sendTo(msgStuff, (EntityPlayerMP) dummy);
				}
			}

			// Add and spawn "Them". As of now, it's just a bunch of invisible
			// endermen. They drop "Lucid Dream Essence."
			// They can be seen by all players, that's alright. They just like
			// to gather near black holes void of sanity.
			// If the player's sanity is really low, spawn a bunch of "Them" and
			// make "Them" attack the player.
			if ((stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.15)) && spawnThemTimer >= 15)
			{

				// Random numbers... gotta love random numbers.
				double randOffsetToSummonThem = Math.random() * 30;
				double posOrNeg = Math.round(Math.random());

				// Reset spawnThemTimer
				spawnThemTimer = 0;

				if (posOrNeg == 0)
				{
					randOffsetToSummonThem = randOffsetToSummonThem * -1;
				}

				// Instance of Them
				EntityEnderman them = new EntityEnderman(dummy.world);

				// Position Them
				them.setLocationAndAngles(playerPosX + randOffsetToSummonThem, playerPosY + 2,
						playerPosZ + randOffsetToSummonThem, 0.0f, 0);

				// Affect Them
				them.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 212121, 1, false, false));

				// Aggroe Them
				them.setAttackTarget((EntityLivingBase) dummy);

				// Add to the "entity limit"... Them
				them.preventEntitySpawning = true;

				// Summon Them
				dummy.world.spawnEntity(them);
			}
			else if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.50))
			{

				// Random numbers... YEE
				double randChanceToSummonThem = Math.random();
				double randOffsetToSummonThem = Math.random() * 30;
				double posOrNeg = Math.round(Math.random());

				if (posOrNeg == 0)
				{
					randOffsetToSummonThem = randOffsetToSummonThem * -1;
				}

				if (randChanceToSummonThem < 0.03)
				{

					// Instance of Them
					EntityEnderman them = new EntityEnderman(dummy.world);

					// Affect Them
					them.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 212121, 1, false, false));

					// Position Them
					them.setLocationAndAngles(playerPosX + randOffsetToSummonThem, playerPosY + 2,
							playerPosZ + randOffsetToSummonThem, 0.0f, 0);

					// Add to the "entity limit"... Them
					them.preventEntitySpawning = true;

					// Summon Them
					dummy.world.spawnEntity(them);
				}
			}
		}
	}

	// This checks any consumed item by the player, and affects sanity
	// accordingly. Just vanilla items for now.
	public void onPlayerConsumeItem(EntityPlayer player, ItemStack item)
	{
		// Capability
		VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);

		// Number of items
		int amount = item.getCount();

		// If raw or bad food, drain sanity.
		if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.CHICKEN, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.BEEF, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.RABBIT, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.MUTTON, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.PORKCHOP, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.FISH, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.ROTTEN_FLESH, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -10.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.SPIDER_EYE, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, -15.0f);
		}

		// If cooked or good food, increase sanity.
		if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_CHICKEN, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_BEEF, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_RABBIT, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_MUTTON, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_PORKCHOP, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_FISH, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.PUMPKIN_PIE, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 15.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKIE, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.RABBIT_STEW, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 15.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.MUSHROOM_STEW, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 10.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.BEETROOT_SOUP, amount)))
		{
			stats.modifyStat(VitalStatType.SANITY, 10.0f);
		}
	}

	// This checks if the player is sleeping.
	// It's mostly for servers, as not everyone may be asleep at the same time.
	// This method alone doesn't run for too long on singleplayer.
	public void onPlayerSleepInBed(EntityPlayer player)
	{
		VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);
		stats.modifyStat(VitalStatType.SANITY, 0.004f);
		// Induce some hunger.
		SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "hunger", 20, 4, false, false);
	}

	// At this point, the player has awoke from their sleep. This "sleep" could've been 1 second or 1 day.
	// Figure out if it is daytime (the sleep is successful). If so, grant extra sanity and drain extra hunger.
	public void onPlayerWakeUp(EntityPlayer player)
	{
		VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);
		
		// Is it daytime? If not, the player just clicked "Leave Bed" or something related to try to cheat the system (and might've succeeded).
		if(player.world.getWorldTime() % 24000 >= 0)
		{
			stats.modifyStat(VitalStatType.SANITY, 33F);
			// Make player hungry for breakfast (or something...).
			player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() - 8);
		}
	}

	// As we know, They will spawn near insane players. They should drop lucid dream essence when killed.
	public void onDropsDropped(Entity entityKilled, List<EntityItem> drops, int lootingLevel, DamageSource damageSource)
	{

		// Was this mob killed by a player? (and server-side).
		if (damageSource.getDamageType().equals("player") && !entityKilled.world.isRemote)
		{

			// Instance of player
			EntityPlayer player = (EntityPlayer) damageSource.getTrueSource();

			// Capability
			VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);

			// Was the victim an enderman? or Them?
			if (entityKilled instanceof EntityEnderman)
			{

				// Now, was the player insane (or insane enough)?
				if(stats.getStat(VitalStatType.SANITY) <= (VitalStatType.SANITY.max * 0.50))
				{
					drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.LUCID_DREAM_ESSENCE.get(), 1)));
					
					double randChanceForAdditional = Math.random();
					if (randChanceForAdditional < 0.50)
					{

						drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.LUCID_DREAM_ESSENCE.get(), 1)));
					}

					// A higher looting level on the weapon will give a chance
					// for more essence to drop.
					for (int i = 0; i < lootingLevel; i++)
					{

						double anotherOne = Math.random();
						if (anotherOne < 0.75)
						{

							drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.LUCID_DREAM_ESSENCE.get(), 1)));
						}
					}
				}

				// The player regains sanity for killing one of their fears.
				stats.modifyStat(VitalStatType.SANITY, 15F);
			}
		}
	}
}