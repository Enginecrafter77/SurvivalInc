package net.schoperation.schopcraft.cap.sanity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpellcasterIllager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;
import net.schoperation.schopcraft.lib.ModItems;
import net.schoperation.schopcraft.packet.PotionEffectPacket;
import net.schoperation.schopcraft.packet.SanityPacket;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.SummonInfoPacket;
import net.schoperation.schopcraft.util.SchopServerEffects;

/*
 * Where sanity is modified.
 */

public class SanityModifier {
	
	// This allows the client to tell the server of any changes to the player's sanity that the server can't detect.
	public static void getClientChange(String uuid, float newSanity, float newMaxSanity, float newMinSanity) {
		
		// basic server variables
		MinecraftServer serverworld = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = serverworld.getCurrentPlayerCount();
		String[] playerlist = serverworld.getOnlinePlayerNames();	
		
		// loop through each player and see if the uuid matches the sent one.
		for (int num = 0; num < playerCount; num++) {
			
			EntityPlayerMP player = serverworld.getPlayerList().getPlayerByUsername(playerlist[num]);
			String playeruuid = player.getCachedUniqueIdString();
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			boolean equalStrings = uuid.equals(playeruuid);
			
			if (equalStrings) {
	
				sanity.increase(newSanity-10);
				sanity.setMax(newMaxSanity);
				sanity.setMin(newMinSanity);
			}
		}
	}
	
	// this is used for the timer at the end of onPlayerUpdate to allow for a hallucination once per 20 ticks.
	// the other is for spawning "Them"
	private static int lucidTimer = 0;
	private static int spawnThemTimer = 0;
	
	public static void onPlayerUpdate(Entity player) {
		
		// get capabilities
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		
		// block position of player
		BlockPos playerPos = player.getPosition();
		
		// actual position of player
		double playerPosX = player.posX;
		double playerPosY = player.posY;
		double playerPosZ = player.posZ;
		
		// list of entities around player
		AxisAlignedBB boundingBox = player.getEntityBoundingBox().grow(7, 2, 7);
		List nearbyMobs = player.world.getEntitiesWithinAABB(EntityMob.class, boundingBox);
		List nearbyAnimals = player.world.getEntitiesWithinAABB(EntityAnimal.class, boundingBox);
		
		// server-side
		if (!player.world.isRemote) {
			
			// being awake late at night is only for crazy people and college students.
			if (!player.world.isDaytime()) {
				
				sanity.decrease(0.002f);
			}
			
			// being in the nether or the end isn't too sane. 
			if (player.dimension == -1 || player.dimension == 1) {
				
				sanity.decrease(0.005f);
			}
			
			// being in the dark in general, is pretty spooky
			if (player.world.getLight(playerPos, true) < 2 && player.dimension != -1 && player.dimension != 1) {
				
				sanity.decrease(0.1f);
			}
			else if (player.world.getLight(playerPos, true) < 4 && player.dimension != -1 && player.dimension != 1) {
				
				sanity.decrease(0.05f);
			}
			else if (player.world.getLight(playerPos, true) < 7 && player.dimension != -1 && player.dimension != 1) {
				
				sanity.decrease(0.01f);
			}
			
			// being drenched for a long time isn't too nice
			if (wetness.getWetness() > 90.0f) {
				
				sanity.decrease(0.006f);
			}
			else if (wetness.getWetness() > 70.0f) {
				
				sanity.decrease(0.004f);
			}
			else if (wetness.getWetness() > 50.0f) {
				
				sanity.decrease(0.002f);
			}
			
			// now iterate through each mob that appears on the list of nearby mobs
			for (int numMobs = 0; numMobs < nearbyMobs.size(); numMobs++) {
				
				// the chosen mob
				EntityMob mob = (EntityMob) nearbyMobs.get(numMobs);
				
				// now change sanity according to what it is
				if (mob instanceof EntityEnderman) {
					
					sanity.decrease(0.005f);
				}
				else if (mob instanceof EntityEvoker || mob instanceof EntityIllusionIllager || mob instanceof EntitySpellcasterIllager || mob instanceof EntityVindicator) {
					
					sanity.decrease(0.004f);
				}
				else if (mob instanceof EntityElderGuardian || mob instanceof EntityGuardian) {
					
					sanity.decrease(0.003f);
				}
				else if (mob instanceof EntityWither) {
					
					sanity.decrease(0.05f);
				}
				else {
					
					sanity.decrease(0.002f);
				}
			}
			
			// do the same for animals.
			for (int numAnimals = 0; numAnimals < nearbyAnimals.size(); numAnimals++) {
				
				// the chosen mob
				EntityAnimal animal = (EntityAnimal) nearbyAnimals.get(numAnimals);
				
				// now change sanity according to what it is
				if (animal instanceof EntityWolf || animal instanceof EntityOcelot || animal instanceof EntityParrot) {
					
					sanity.increase(0.005f);
				}
				else if (animal instanceof EntitySheep) {
					
					sanity.increase(0.003f);
				}
				else {
					
					sanity.increase(0.002f);
				}
			}
			
			// send sanity packet to client for rendering
			IMessage msg = new SanityPacket.SanityMessage(player.getCachedUniqueIdString(), sanity.getSanity(), sanity.getMaxSanity(), sanity.getMinSanity());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP) player);
			
			// ===========================================================================
			//                  The Side Effects of Insanity
			// ===========================================================================
			
			// every 20 ticks (1 second) there is a chance for a hallucination to appear; visual, audial, or both.
			// in this case, a hallucination is a client-only particle/sound. The "things" (Maxwell refers to them as "Them") are a different area.
			// the more insane the player is, the bigger the chance is.
			if (lucidTimer < 20) {
				
				// increment timer until it reaches 20
				lucidTimer++;
			}
			else {
				
				// reset timer
				lucidTimer = 0;
				
				// increment THIS timer
				spawnThemTimer++;
				
				// there'll only be hallucinations for players with less than 70% of their sanity.
				if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.70)) {
					
					// determine if a hallucination should appear
					double chanceOfHallucination = (double) (sanity.getSanity() / 100) + 0.30;
					double randomLucidNumber = Math.random();
					boolean shouldSpawnHallucination = chanceOfHallucination < randomLucidNumber;
					
					// so... should one appear?
					if (shouldSpawnHallucination) {
						
						// now pick one... more random numbers
						double pickAHallucination = Math.random();
						double randOffset = Math.random() * 6;
						int posOrNeg = (int) Math.round(Math.random());
						
						if (posOrNeg == 0) { randOffset = randOffset * -1; }

						// as of now... ten possibilities... all weighted equally.
						// These will be called on the client, so no one else can see/hear them. Random positions nearby the player too.
						// Enderman noise + particles
						if (pickAHallucination >= 0 && pickAHallucination < 0.10) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "EndermanSound", "EndermanParticles", playerPosX+randOffset, playerPosY+1, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						// Zombie sound
						else if (pickAHallucination >= 0.10 && pickAHallucination < 0.20) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "ZombieSound", "null", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						// Ghast sound
						else if (pickAHallucination >= 0.20 && pickAHallucination < 0.30) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "GhastSound", "null", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player); 
						}
						// explosion sound + particles
						else if (pickAHallucination >= 0.30 && pickAHallucination < 0.40) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "ExplosionSound", "ExplosionParticles", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						// stone sound
						else if (pickAHallucination >= 0.40 && pickAHallucination < 0.50) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "StoneBreakSound", "null", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						// mist in the air... tf???????
						else if (pickAHallucination >= 0.50 && pickAHallucination < 0.60) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "null", "CreepyMistParticles", playerPosX+randOffset, playerPosY+1, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						// a guardian appearing in your face. This one still scares the crap out of me.
						else if (pickAHallucination >= 0.60 && pickAHallucination < 0.70) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "null", "GuardianParticles", playerPosX, playerPosY, playerPosZ);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						// fire sounds + smoke particles
						else if (pickAHallucination >= 0.70 && pickAHallucination < 0.80) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "FireSound", "SmokeParticles", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						// villager sound... are they lost?
						else if (pickAHallucination >= 0.80 && pickAHallucination < 0.90) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "VillagerSound", "null", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						// lava sound
						else if (pickAHallucination >= 0.90 && pickAHallucination <= 1.00) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "LavaSound", "null", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
					}
				}
				// There are other side effects of insanity other than hallucinations.
				// Here, the player's view is wobbled/distorted
				// Some weird ambience is added to make insanity feel more insane. And... weird. It's just the right word.
				// Also, they may come and attack you.
				
				// make the screen of the insane player wobble
				if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.35)) {
					
					SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "nausea", 100, 5, false, false);
				}
				
				// add some weird insanity ambience
				if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.20)) {
					
					// random chance so it doesn't overlap with itself
					double randInsanityAmbience = Math.random();
					
					if (randInsanityAmbience < 0.20) {
						
						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "InsanityAmbienceSoundLoud", "null", playerPosX, playerPosY, playerPosZ);
						SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}
				}
				else if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.50)) {
					
					// random chance so it doesn't overlap with itself
					double randInsanityAmbience = Math.random();
					
					if (randInsanityAmbience < 0.20) {
						
						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "InsanityAmbienceSound", "null", playerPosX, playerPosY, playerPosZ);
						SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}
				}
				
				// add and spawn "Them". As of now, it's just a bunch of invisible endermen. They drop "Lucid Dream Essence"
				// They can be seen by all players, that's alright. They just like to gather near black holes void of sanity.
				// if the player's sanity is really low, spawn a bunch of "Them" and make "Them" attack the player.
				if ((sanity.getSanity() <= (sanity.getMaxSanity() * 0.15)) && spawnThemTimer >= 10) {
					
					// random numbers... yee
					double randOffsetToSummonThem = Math.random() * 30;
					double posOrNeg = Math.round(Math.random());
					
					// reset spawnThemTimer
					spawnThemTimer = 0;
					
					if (posOrNeg == 0) { randOffsetToSummonThem = randOffsetToSummonThem * -1; }
					
					// instance of Them
					EntityEnderman them = new EntityEnderman(player.world);

					// position Them
					them.setLocationAndAngles(playerPosX+randOffsetToSummonThem, playerPosY+2, playerPosZ+randOffsetToSummonThem, 0.0f, 0);
					
					// affect Them
					them.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 212121, 1, false, false));
					
					// aggroe Them
					them.setAttackTarget((EntityLivingBase) player);
					
					// summon Them
					player.world.spawnEntity(them);	
				}
				
				// otherwise just rarely summon them
				else if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.50)) {
					
					// random numbers... yee
					double randChanceToSummonThem = Math.random();
					double randOffsetToSummonThem = Math.random() * 30;
					double posOrNeg = Math.round(Math.random());
					
					if (posOrNeg == 0) { randOffsetToSummonThem = randOffsetToSummonThem * -1; }
					
					if (randChanceToSummonThem < 0.05) {
						
						// instance of Them
						EntityEnderman them = new EntityEnderman(player.world);
						
						// affect Them
						them.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 212121, 1, false, false));
						
						// position Them
						them.setLocationAndAngles(playerPosX+randOffsetToSummonThem, playerPosY+2, playerPosZ+randOffsetToSummonThem, 0.0f, 0);

						// summon Them
						player.world.spawnEntity(them);
					}
				}
			}
		}
	}
	
	// This checks any consumed item by the player, and affects sanity accordingly. Just vanilla items for now.
	public static void onPlayerConsumeItem(EntityPlayer player, ItemStack item) {
		
		// capability
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		
		// server-side
		if (!player.world.isRemote) {
						
			// if raw or bad food, drain sanity
			if (item.areItemStacksEqual(item, new ItemStack(Items.CHICKEN))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.BEEF))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.RABBIT))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.MUTTON))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.PORKCHOP))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.FISH))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.ROTTEN_FLESH))) { sanity.decrease(10.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.SPIDER_EYE))) { sanity.decrease(15.0f); }
			
			// if cooked or good food, increase sanity
			if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_CHICKEN))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_BEEF))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_RABBIT))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_MUTTON))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_PORKCHOP))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_FISH))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.PUMPKIN_PIE))) { sanity.increase(15.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKIE))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.RABBIT_STEW))) { sanity.increase(15.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.MUSHROOM_STEW))) { sanity.increase(10.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.BEETROOT_SOUP))) { sanity.increase(10.0f); }
			
			// send data to client for rendering
			IMessage msg = new SanityPacket.SanityMessage(player.getCachedUniqueIdString(), sanity.getSanity(), sanity.getMaxSanity(), sanity.getMinSanity());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP) player);
		}	
	}
	
	// this checks if the player is sleeping on a server, since not everyone may be sleeping at the same time.
	// this pretty much will not be fired if the world is singleplayer, as by the time the player is fully asleep,
	// ...the time will be day, kicking the player out of bed. Called on client-side, because when I tried to do it server-side...
	// ...only one player would get the +33 sanity from waking up, even if more than one player woke up.
	public static void onPlayerSleepInBed(EntityPlayer player) {
		
		// capability
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		
		// client-side
		if (player.world.isRemote) {
			
			sanity.set(10.0f);
			sanity.increase(0.008f);
			
			// induce hunger on the sleeping player
			IMessage msgEffect = new PotionEffectPacket.PotionEffectMessage(player.getCachedUniqueIdString(), "hunger", 20, 4, false, false);
			SchopPackets.net.sendToServer(msgEffect);
			
			// send data to server
			IMessage msg = new SanityPacket.SanityMessage(player.getCachedUniqueIdString(), sanity.getSanity(), sanity.getMaxSanity(), sanity.getMinSanity());
			SchopPackets.net.sendToServer(msg);
		}
	}
	
	// At this point, the player has awoke from their sleep. This "sleep" could've been 1 second or 1 day.
	// Figure out if it is daytime (the sleep is successful). If so, grant extra sanity and drain extra hunger.
	public static void onPlayerWakeUp(EntityPlayer player) {
		
		// capability
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		
		// is it daytime? If not, the player just clicked "Leave Bed" or something related to try to cheat the system (and might've succeeded)
		if (player.world.isRemote && player.world.getSunBrightnessFactor(1.0f) > 0.65f) {
			
			sanity.set(10.0f);
			sanity.increase(33f);
			
			// make player hungry for breakfast (or something...)
			IMessage msgEffect = new PotionEffectPacket.PotionEffectMessage(player.getCachedUniqueIdString(), "hunger", 200, 8, false, false);
			SchopPackets.net.sendToServer(msgEffect);

			// send data to server
			IMessage msg = new SanityPacket.SanityMessage(player.getCachedUniqueIdString(), sanity.getSanity(), sanity.getMaxSanity(), sanity.getMinSanity());
			SchopPackets.net.sendToServer(msg);
		}
	}
	
	// As we know, They will spawn near insane players. They should drop lucid dream essence when killed.
	public static void onDropsDropped(Entity entityKilled, List<EntityItem> drops, int lootingLevel, DamageSource damageSource) {
		
		// Was this mob killed by a player? (and server-side)
		if (damageSource.getDamageType() == "player" && !entityKilled.world.isRemote) {
			
			// instance of player
			EntityPlayer player = (EntityPlayer) damageSource.getTrueSource();
			
			// sanity capability
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			
			// was the victim an enderman? or Them?
			if (entityKilled instanceof EntityEnderman) {
				
				// now, was the player insane (or insane enough)?
				if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.50)) {
					
					// drop some essence. 50% chance for an extra essence.
					int sizeOfList = drops.size();
					drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.ITEMS[3], 1)));
					
					double randChanceForAdditional = Math.random();
					if (randChanceForAdditional < 0.50) {
						
						drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.ITEMS[3], 1)));
					}	
				}
				
				// The player regains sanity for killing one of their fears.
				sanity.increase(15.0f);
			}	
		}
	}
}
