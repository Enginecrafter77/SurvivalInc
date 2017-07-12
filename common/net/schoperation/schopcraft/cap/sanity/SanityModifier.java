package net.schoperation.schopcraft.cap.sanity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;
import net.schoperation.schopcraft.packet.SanityPacket;
import net.schoperation.schopcraft.packet.SchopPackets;

/*
 * Where sanity is modified.
 */

public class SanityModifier {
	
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
				
				sanity.decrease(0.006f);
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
		}
	}
}
