package net.schoperation.schopcraft.cap.sanity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpellcasterIllager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
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
import net.schoperation.schopcraft.cap.ghost.GhostProvider;
import net.schoperation.schopcraft.cap.ghost.IGhost;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;
import net.schoperation.schopcraft.config.SchopConfig;
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
	// TODO Redo this clientchange packet system thing. 
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
	
	// The first variable is used for the timer at the end of onPlayerUpdate to allow for a hallucination once per 20 ticks.
	// The other is for spawning "Them".
	private int lucidTimer = 0;
	private int spawnThemTimer = 0;
	
	public void onPlayerUpdate(Entity player) {
		
		// Capabilities
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		
		// Block position of player.
		BlockPos playerPos = player.getPosition();
		
		// ACTUAL position of player.
		double playerPosX = player.posX;
		double playerPosY = player.posY;
		double playerPosZ = player.posZ;
		
		// Lists of entities near the player.
		AxisAlignedBB boundingBox = player.getEntityBoundingBox().grow(7, 2, 7);
		AxisAlignedBB boundingBoxPlayers = player.getEntityBoundingBox().grow(4, 2, 4);
		List nearbyMobs = player.world.getEntitiesWithinAABB(EntityMob.class, boundingBox);
		List nearbyAnimals = player.world.getEntitiesWithinAABB(EntityAnimal.class, boundingBox);
		List nearbyPlayers = player.world.getEntitiesWithinAABB(EntityPlayer.class, boundingBoxPlayers);
		List nearbyVillagers = player.world.getEntitiesWithinAABB(EntityVillager.class, boundingBoxPlayers);
		
		// Server-side.
		if (!player.world.isRemote) {
			
			// Modifier from config
			float modifier = (float) SchopConfig.MECHANICS.sanityScale;
			
			// Being awake late at night is only for crazy people and college students.
			if (!player.world.isDaytime() && playerPosY >= player.world.getSeaLevel()) {
				
				sanity.decrease(0.0015f * modifier);
			}
			
			// Being in the nether or the end isn't too sane. 
			if (player.dimension == -1 || player.dimension == 1) {
				
				sanity.decrease(0.004f * modifier);
			}
			
			// Constant drain in caves... because why not!
			if (playerPosY <= (player.world.getSeaLevel() - 15)) {
				
				sanity.decrease(0.0015f * modifier);
			}
			
			// Being in the dark in general, is pretty spooky.
			if (player.world.getLight(playerPos, true) < 2 && player.dimension != -1 && player.dimension != 1) {
				
				sanity.decrease(0.08f * modifier);
			}
			
			else if (player.world.getLight(playerPos, true) < 4 && player.dimension != -1 && player.dimension != 1) {
				
				sanity.decrease(0.04f * modifier);
			}
			
			else if (player.world.getLight(playerPos, true) < 7 && player.dimension != -1 && player.dimension != 1 && (playerPosY <= player.world.getSeaLevel())) {
				
				sanity.decrease(0.02f * modifier);
			}
			
			// Being drenched for a long time won't do you good.
			if (wetness.getWetness() > 90.0f) {
				
				sanity.decrease(0.003f * modifier);
			}
			
			else if (wetness.getWetness() > 70.0f) {
				
				sanity.decrease(0.001f * modifier);
			}
			
			// Now iterate through each mob that appears on the list of nearby mobs.
			for (int numMobs = 0; numMobs < nearbyMobs.size(); numMobs++) {
				
				// Chosen mob
				EntityMob mob = (EntityMob) nearbyMobs.get(numMobs);
				
				// Now change sanity according to what it is.
				if (mob instanceof EntityEnderman) {
					
					sanity.decrease(0.005f * modifier);
				}
				
				else if (mob instanceof EntityEvoker || mob instanceof EntityIllusionIllager || mob instanceof EntitySpellcasterIllager || mob instanceof EntityVindicator) {
					
					sanity.decrease(0.004f * modifier);
				}
				
				else if (mob instanceof EntityWither) {
					
					sanity.decrease(0.05f * modifier);
				}
				
				else {
					
					sanity.decrease(0.003f * modifier);
				}
			}
			
			// Do the same for animals.
			for (int numAnimals = 0; numAnimals < nearbyAnimals.size(); numAnimals++) {
				
				// Chosen animal
				EntityAnimal animal = (EntityAnimal) nearbyAnimals.get(numAnimals);
				
				// Now change sanity according to what it is.
				if (animal instanceof EntityWolf || animal instanceof EntityOcelot || animal instanceof EntityParrot) {
					
					sanity.increase(0.005f * modifier);
				}
				
				else if (animal instanceof EntitySheep) {
					
					sanity.increase(0.003f * modifier);
				}
				
				else {
					
					sanity.increase(0.002f * modifier);
				}
			}
			
			// And for players.
			for (int numPlayers = 0; numPlayers < nearbyPlayers.size(); numPlayers++) {
				
				// Chosen player
				EntityPlayerMP otherPlayer = (EntityPlayerMP) nearbyPlayers.get(numPlayers);
				
				// Ghost capability of other player.
				IGhost ghost = otherPlayer.getCapability(GhostProvider.GHOST_CAP, null);
				
				// Now change sanity, unless it's just the player themselves, or a ghost.
				if (otherPlayer != player && !ghost.isGhost()) {
					
					sanity.increase(0.003f * modifier);
				}
				
				else if (otherPlayer != player && ghost.isGhost()) {
					
					sanity.decrease(0.05f * modifier);
				}
			}
			
			// Villagers are nice as well.
			for (int numVillagers = 0; numVillagers < nearbyVillagers.size(); numVillagers++) {
				
				sanity.increase(0.003f * modifier);
			}
			
			// ===========================================================================
			//                  The Side Effects of Insanity
			// ===========================================================================
			
			// Every 20 ticks (1 second) there is a chance for a hallucination to appear; visual, audial, or both.
			// In this case, a hallucination is a client-only particle/sound. The "things" (Maxwell refers to them as "Them") are a different area.
			// The more insane the player is, the bigger the chance is.
			if (lucidTimer < 20) {
				
				// Increment timer until it reaches 20.
				lucidTimer++;
			}
			else {
				
				// Reset timer
				lucidTimer = 0;
				
				// Increment THIS timer
				spawnThemTimer++;
				
				// There'll only be hallucinations for players with less than 70% of their sanity.
				if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.70)) {
					
					// Determine if a hallucination should appear.
					double chanceOfHallucination = (double) (sanity.getSanity() / 100) + 0.30;
					double randomLucidNumber = Math.random();
					boolean shouldSpawnHallucination = chanceOfHallucination < randomLucidNumber;
					
					// So... should one appear?
					if (shouldSpawnHallucination) {
						
						// Now pick one... more random numbers!
						double pickAHallucination = Math.random();
						double randOffset = Math.random() * 6;
						int posOrNeg = (int) Math.round(Math.random());
						
						if (posOrNeg == 0) { randOffset = randOffset * -1; }

						// As of now... ten possibilities... all weighted equally.
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
						
						// Explosion sound + particles
						else if (pickAHallucination >= 0.30 && pickAHallucination < 0.40) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "ExplosionSound", "ExplosionParticles", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						
						// Stone sound
						else if (pickAHallucination >= 0.40 && pickAHallucination < 0.50) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "StoneBreakSound", "null", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						
						// Mist in the air... tf???????
						else if (pickAHallucination >= 0.50 && pickAHallucination < 0.60) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "null", "CreepyMistParticles", playerPosX+randOffset, playerPosY+1, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						
						// A guardian appearing in your face. This one still scares the crap out of me.
						else if (pickAHallucination >= 0.60 && pickAHallucination < 0.70) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "null", "GuardianParticles", playerPosX, playerPosY, playerPosZ);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						
						// Fire sounds + smoke particles
						else if (pickAHallucination >= 0.70 && pickAHallucination < 0.80) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "FireSound", "SmokeParticles", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						
						// A�villager sound... are they lost?
						else if (pickAHallucination >= 0.80 && pickAHallucination < 0.90) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "VillagerSound", "null", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
						
						// Lava sound
						else if (pickAHallucination >= 0.90 && pickAHallucination <= 1.00) {
							
							IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "LavaSound", "null", playerPosX+randOffset, playerPosY+randOffset, playerPosZ+randOffset);
							SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
						}
					}
				}
				
				// There are other side effects of insanity other than hallucinations.
				// Here, the player's view is wobbled/distorted
				// Some weird ambience is added to make insanity feel more insane. And... weird. It's just the right word.
				// Also, They may come and attack you.
				
				// Make the screen of the insane player wobble.
				if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.35)) {
					
					SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "nausea", 100, 5, false, false);
				}
				
				// Add some weird insanity ambiance.
				if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.20)) {
					
					// Random chance so it doesn't overlap with itself.
					double randInsanityAmbience = Math.random();
					
					if (randInsanityAmbience < 0.20) {
						
						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "InsanityAmbienceSoundLoud", "null", playerPosX, playerPosY, playerPosZ);
						SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}
				}
				else if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.50)) {
					
					// Random chance so it doesn't overlap with itself.
					double randInsanityAmbience = Math.random();
					
					if (randInsanityAmbience < 0.20) {
						
						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "InsanityAmbienceSound", "null", playerPosX, playerPosY, playerPosZ);
						SchopPackets.net.sendTo(msgStuff, (EntityPlayerMP) player);
					}
				}
				
				// Add and spawn "Them". As of now, it's just a bunch of invisible endermen. They drop "Lucid Dream Essence."
				// They can be seen by all players, that's alright. They just like to gather near black holes void of sanity.
				// If the player's sanity is really low, spawn a bunch of "Them" and make "Them" attack the player.
				if ((sanity.getSanity() <= (sanity.getMaxSanity() * 0.15)) && spawnThemTimer >= 15) {
					
					// Random numbers... gotta love random numbers.
					double randOffsetToSummonThem = Math.random() * 30;
					double posOrNeg = Math.round(Math.random());
					
					// Reset spawnThemTimer
					spawnThemTimer = 0;
					
					if (posOrNeg == 0) { randOffsetToSummonThem = randOffsetToSummonThem * -1; }
					
					// Instance of Them
					EntityEnderman them = new EntityEnderman(player.world);

					// Position Them
					them.setLocationAndAngles(playerPosX+randOffsetToSummonThem, playerPosY+2, playerPosZ+randOffsetToSummonThem, 0.0f, 0);
					
					// Affect Them
					them.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 212121, 1, false, false));
					
					// Aggroe Them
					them.setAttackTarget((EntityLivingBase) player);
					
					// Add to the "entity limit"... Them
					them.preventEntitySpawning = true;
					
					// Summon Them
					player.world.spawnEntity(them);	
				}
				
				// Otherwise just rarely summon them.
				else if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.50)) {
					
					// Random numbers... YEE
					double randChanceToSummonThem = Math.random();
					double randOffsetToSummonThem = Math.random() * 30;
					double posOrNeg = Math.round(Math.random());
					
					if (posOrNeg == 0) { randOffsetToSummonThem = randOffsetToSummonThem * -1; }
					
					if (randChanceToSummonThem < 0.03) {
						
						// Instance of Them
						EntityEnderman them = new EntityEnderman(player.world);
						
						// Affect Them
						them.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 212121, 1, false, false));
						
						// Position Them
						them.setLocationAndAngles(playerPosX+randOffsetToSummonThem, playerPosY+2, playerPosZ+randOffsetToSummonThem, 0.0f, 0);
						
						// Add to the "entity limit"... Them
						them.preventEntitySpawning = true;

						// Summon Them
						player.world.spawnEntity(them);
					}
				}
			}
		}
	}
	
	// This checks any consumed item by the player, and affects sanity accordingly. Just vanilla items for now.
	public void onPlayerConsumeItem(EntityPlayer player, ItemStack item) {
		
		// Capability
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		
		// Server-side.
		if (!player.world.isRemote) {
			
			// Number of items
			int amount = item.getCount();
			
			// If raw or bad food, drain sanity.
			if (item.areItemStacksEqual(item, new ItemStack(Items.CHICKEN, amount))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.BEEF, amount))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.RABBIT, amount))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.MUTTON, amount))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.PORKCHOP, amount))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.FISH, amount))) { sanity.decrease(5.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.ROTTEN_FLESH, amount))) { sanity.decrease(10.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.SPIDER_EYE, amount))) { sanity.decrease(15.0f); }
			
			// If cooked or good food, increase sanity.
			if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_CHICKEN, amount))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_BEEF, amount))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_RABBIT, amount))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_MUTTON, amount))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_PORKCHOP, amount))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKED_FISH, amount))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.PUMPKIN_PIE, amount))) { sanity.increase(15.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.COOKIE, amount))) { sanity.increase(2.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.RABBIT_STEW, amount))) { sanity.increase(15.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.MUSHROOM_STEW, amount))) { sanity.increase(10.0f); }
			else if (item.areItemStacksEqual(item, new ItemStack(Items.BEETROOT_SOUP, amount))) { sanity.increase(10.0f); }
		}	
	}
	
	// This checks if the player is sleeping on a server, since not everyone may be sleeping at the same time.
	// This pretty much will not be fired if the world is singleplayer, as by the time the player is fully asleep,
	// ...the time will be day, kicking the player out of bed. Called on client-side, because when I tried to do it server-side...
	// ...only one player would get the +33 sanity from waking up, even if more than one player woke up.
	public void onPlayerSleepInBed(EntityPlayer player) {
		
		// Capability
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		
		// Client-side
		if (player.world.isRemote) {
			
			sanity.set(10.0f);
			sanity.increase(0.008f);
			
			// Induce hunger on the sleeping player.
			IMessage msgEffect = new PotionEffectPacket.PotionEffectMessage(player.getCachedUniqueIdString(), "hunger", 20, 4, false, false);
			SchopPackets.net.sendToServer(msgEffect);
			
			// Send sanity data to server.
			IMessage msg = new SanityPacket.SanityMessage(player.getCachedUniqueIdString(), sanity.getSanity(), sanity.getMaxSanity(), sanity.getMinSanity());
			SchopPackets.net.sendToServer(msg);
		}
	}
	
	// At this point, the player has awoke from their sleep. This "sleep" could've been 1 second or 1 day.
	// Figure out if it is daytime (the sleep is successful). If so, grant extra sanity and drain extra hunger.
	public void onPlayerWakeUp(EntityPlayer player) {
		
		// Capability
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		
		// Is it daytime? If not, the player just clicked "Leave Bed" or something related to try to cheat the system (and might've succeeded).
		if (player.world.isRemote && player.world.getSunBrightnessFactor(1.0f) > 0.65f) {
			
			sanity.set(10.0f);
			sanity.increase(33f);
			
			// Make player hungry for breakfast (or something...).
			IMessage msgEffect = new PotionEffectPacket.PotionEffectMessage(player.getCachedUniqueIdString(), "hunger", 200, 10, false, false);
			SchopPackets.net.sendToServer(msgEffect);

			// Send sanity data to server.
			IMessage msg = new SanityPacket.SanityMessage(player.getCachedUniqueIdString(), sanity.getSanity(), sanity.getMaxSanity(), sanity.getMinSanity());
			SchopPackets.net.sendToServer(msg);
		}
	}
	
	// As we know, They will spawn near insane players. They should drop lucid dream essence when killed.
	public void onDropsDropped(Entity entityKilled, List<EntityItem> drops, int lootingLevel, DamageSource damageSource) {
		
		// Was this mob killed by a player? (and server-side).
		if (damageSource.getDamageType().equals("player") && !entityKilled.world.isRemote) {
			
			// Instance of player
			EntityPlayer player = (EntityPlayer) damageSource.getTrueSource();
			
			// Capability
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			
			// Was the victim an enderman? or Them?
			if (entityKilled instanceof EntityEnderman) {
				
				// Now, was the player insane (or insane enough)?
				if (sanity.getSanity() <= (sanity.getMaxSanity() * 0.50)) {
										
					// Drop some essence. 50% chance for an extra essence (by default).
					int sizeOfList = drops.size();
					drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.LUCID_DREAM_ESSENCE, 1)));
					
					double randChanceForAdditional = Math.random();
					if (randChanceForAdditional < 0.50) {
						
						drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.LUCID_DREAM_ESSENCE, 1)));
					}
					
					// A higher looting level on the weapon will give a chance for more essence to drop.
					for (int i = 0; i < lootingLevel; i++) {
						
						double anotherOne = Math.random();
						if (anotherOne < 0.75) {
							
							drops.add(new EntityItem(player.world, entityKilled.posX, entityKilled.posY, entityKilled.posZ, new ItemStack(ModItems.LUCID_DREAM_ESSENCE, 1)));
						}
					}
				}
				
				// The player regains sanity for killing one of their fears.
				sanity.increase(15.0f);
			}	
		}
	}
}