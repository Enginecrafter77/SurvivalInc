package schoperation.schopcraft.cap.temperature;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import schoperation.schopcraft.cap.ghost.GhostProvider;
import schoperation.schopcraft.cap.ghost.IGhost;
import schoperation.schopcraft.cap.wetness.IWetness;
import schoperation.schopcraft.cap.wetness.WetnessProvider;
import schoperation.schopcraft.config.SchopConfig;
import schoperation.schopcraft.lib.ModDamageSources;
import schoperation.schopcraft.util.ProximityDetect;
import schoperation.schopcraft.util.SchopServerEffects;
import schoperation.schopcraft.util.SchopServerParticles;

import java.util.Iterator;
import java.util.List;

/*
 * Where temperature is directly and indirectly modified.
 */

public class TemperatureModifier {

	public void onPlayerUpdate(EntityPlayer player)
	{

		// Capabilities
		ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);

		// Player's coordinates.
		double playerPosX = player.posX;
		double playerPosY = player.posY;
		double playerPosZ = player.posZ;

		// Player's Block position.
		BlockPos blockPos = player.getPosition();

		// =============================================
		// FIRST FACTOR: THE BIOME
		// =============================================
		// Technically, the temperature of the biome. The temperature for the
		// plains biome is 0.8. Desert is 2.0. Cold Taiga is -0.5. Blah blah
		// blah

		// Biome at player position.
		Biome biome = player.world.getBiome(blockPos);

		// Biome's temperature.
		float biomeTemp = biome.getTemperature(blockPos);

		// Some cold biomes are too cold. Make them not so.
		// The hot biomes are too hot for the newTargetTemp below. Make them a
		// bit less hot.
		if (biomeTemp < -0.2)
		{

			biomeTemp = -0.2f;
		}

		else if (biomeTemp > 1.5)
		{

			biomeTemp = 1.5f;
		}

		// If in a cave, stick with a cool, constant temperature
		if (playerPosY <= (player.world.getSeaLevel() - 15))
		{

			biomeTemp = 0.7f;
		}

		// New target temperature based on biome. This constant right here could
		// change. Who knows.
		float newTargetTemp = 78 * biomeTemp;

		// Set it. any other factor will either add to it or take from it.
		temperature.setTarget(newTargetTemp);

		// =====================================================
		// MISC. FACTORS AFFECTING TARGET TEMP
		// =====================================================

		// Is the player directly in the sun?
		if (player.world.isDaytime() && player.world.canBlockSeeSky(blockPos))
		{

			temperature.increaseTarget(10.0f);
		}

		else if (!player.world.isDaytime() && playerPosY >= (player.world.getSeaLevel() - 10))
		{

			temperature.decreaseTarget(10.0f);
		}

		else if (!player.world.canBlockSeeSky(blockPos))
		{

			temperature.decreaseTarget(5.0f);
		}

		// Is the player in the rain?
		if (player.isWet())
		{

			temperature.decreaseTarget(15.0f);
		}

		// Cool down the player if they're wet.
		temperature.decreaseTarget(wetness.getWetness() * 0.40f);

		// ==================================
		// PROXIMITY DETECT
		// ==================================

		// These blocks of if-statements are used to detect blocks near the
		// player, either warming them or cooling them.
		// Check if the player is near fire. Warm the player.
		if (ProximityDetect.isBlockNextToPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.FIRE, player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(20.0f);
			}
			else
			{
				temperature.increaseTarget(30.0f);
			}
		}
		else if (ProximityDetect.isBlockNearPlayer2(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.FIRE,
				player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(10.0f);
			}
			else
			{
				temperature.increaseTarget(15.0f);
			}
		}
		else if (ProximityDetect.isBlockUnderPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.FIRE,
				player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(20.0f);
			}
			else
			{
				temperature.increaseTarget(30.0f);
			}
		}
		else if (ProximityDetect.isBlockUnderPlayer2(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.FIRE,
				player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(10.0f);
			}
			else
			{
				temperature.increaseTarget(15.0f);
			}
		}
		else if (ProximityDetect.isBlockAtPlayerFace(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.FIRE,
				player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(20.0f);
			}
			else
			{
				temperature.increaseTarget(30.0f);
			}
		}
		else if (ProximityDetect.isBlockAtPlayerFace2(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.FIRE,
				player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(10.0f);
			}
			else
			{
				temperature.increaseTarget(15.0f);
			}
		}

		// Lava. Warm the player.
		if (ProximityDetect.isBlockNextToPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.LAVA, player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(40.0f);
			}
			else
			{
				temperature.increaseTarget(45.0f);
			}
		}
		else if (ProximityDetect.isBlockNearPlayer2(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.LAVA,
				player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(20.0f);
			}
			else
			{
				temperature.increaseTarget(25.0f);
			}
		}
		else if (ProximityDetect.isBlockUnderPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.LAVA,
				player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(40.0f);
			}
			else
			{
				temperature.increaseTarget(45.0f);
			}
		}
		else if (ProximityDetect.isBlockUnderPlayer2(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.LAVA,
				player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(20.0f);
			}
			else
			{
				temperature.increaseTarget(25.0f);
			}
		}
		else if (ProximityDetect.isBlockNextToPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				Blocks.FLOWING_LAVA, player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(30.0f);
			}
			else
			{
				temperature.increaseTarget(35.0f);
			}
		}
		else if (ProximityDetect.isBlockNearPlayer2(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				Blocks.FLOWING_LAVA, player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(15.0f);
			}
			else
			{
				temperature.increaseTarget(20.0f);
			}
		}
		else if (ProximityDetect.isBlockUnderPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				Blocks.FLOWING_LAVA, player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(30.0f);
			}
			else
			{
				temperature.increaseTarget(35.0f);
			}
		}
		else if (ProximityDetect.isBlockUnderPlayer2(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				Blocks.FLOWING_LAVA, player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(15.0f);
			}
			else
			{
				temperature.increaseTarget(20.0f);
			}
		}

		// Lit furnace. could act as a heater for now. same y-level only. And at
		// the face.
		if (ProximityDetect.isBlockNextToPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.LIT_FURNACE,
				player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(15.0f);
			}
			else
			{
				temperature.increaseTarget(30.0f);
			}
		}
		else if (ProximityDetect.isBlockNearPlayer2(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				Blocks.LIT_FURNACE, player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(7.5f);
			}
			else
			{
				temperature.increaseTarget(15.0f);
			}
		}
		else if (ProximityDetect.isBlockAtPlayerFace(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				Blocks.LIT_FURNACE, player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(15.0f);
			}
			else
			{
				temperature.increaseTarget(30.0f);
			}
		}
		else if (ProximityDetect.isBlockAtPlayerFace2(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
				Blocks.LIT_FURNACE, player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(7.5f);
			}
			else
			{
				temperature.increaseTarget(15.0f);
			}
		}

		// Magma block. One y-level down only.
		if (ProximityDetect.isBlockUnderPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.MAGMA, player))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(10.0f);
			}
			else
			{
				temperature.increaseTarget(20.0f);
			}
		}
		else if (ProximityDetect.isBlockUnderPlayer2(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Blocks.MAGMA,
				player, false))
		{
			if (player.world.canBlockSeeSky(blockPos))
			{
				temperature.increaseTarget(5.0f);
			}
			else
			{
				temperature.increaseTarget(10.0f);
			}
		}

		// Huddling with other players will warm you up.
		// Ghosts do the opposite.
		// Bounding box and list of nearby players.
		AxisAlignedBB boundingBoxPlayers = player.getEntityBoundingBox().grow(1, 1, 1);
		List<EntityPlayer> nearbyPlayers = player.world.getEntitiesWithinAABB(EntityPlayer.class, boundingBoxPlayers);

		// Now iterate through the list.
		for (int numPlayers = 0; numPlayers < nearbyPlayers.size(); numPlayers++)
		{

			// Chosen player
			EntityPlayerMP otherPlayer = (EntityPlayerMP) nearbyPlayers.get(numPlayers);

			// Ghost capability of other player.
			IGhost ghost = otherPlayer.getCapability(GhostProvider.GHOST_CAP, null);

			// Now change temperature accordingly.
			if (otherPlayer != player && !ghost.isGhost())
			{

				temperature.increaseTarget(5.0f);
			}

			else if (otherPlayer != player && ghost.isGhost())
			{

				temperature.decreaseTarget(20.0f);
			}
		}

		// ================================
		// ARMOR
		// ================================

		// What is the player wearing? If it's leather, then it warms the
		// player. Otherwise, it could go either way.
		// List of armor items.
		Iterator<ItemStack> armorList = player.getArmorInventoryList().iterator();

		// Iterate through items. 0 = boots, 1 = leggings, 2 = chestplate, 3 =
		// helmet.
		while (armorList.hasNext())
		{

			// Element
			ItemStack element = armorList.next();

			// Some float to be added when armor is some metal
			float addedTemp = 0.0f;

			// This will determine whether metal armor will increase or decrease
			// the target temp.
			if (temperature.getTargetTemperature() > 40.0f && temperature.getTargetTemperature() < 60.0f)
			{

				addedTemp = 0.5f;
			}

			else if (temperature.getTargetTemperature() < 40.0f)
			{

				addedTemp = -2.5f;
			}

			else
			{

				addedTemp = 2.5f;
			}

			// Now see what armor it is!
			// Leather. This area is different because of the different colors.
			// It's weird. So gotta use unlocalized names for now.
			if (element.getUnlocalizedName().equals("item.bootsCloth"))
			{
				temperature.increaseTarget(4.0f);
			}
			else if (element.getUnlocalizedName().equals("item.leggingsCloth"))
			{
				temperature.increaseTarget(6.0f);
			}
			else if (element.getUnlocalizedName().equals("item.chestplateCloth"))
			{
				temperature.increaseTarget(10.0f);
			}
			else if (element.getUnlocalizedName().equals("item.helmetCloth"))
			{
				temperature.increaseTarget(5.0f);
			}

			// Chain
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.CHAINMAIL_BOOTS)))
			{
				temperature.increaseTarget(addedTemp);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.CHAINMAIL_LEGGINGS)))
			{
				temperature.increaseTarget(addedTemp * 1.5f);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.CHAINMAIL_CHESTPLATE)))
			{
				temperature.increaseTarget(addedTemp * 2);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.CHAINMAIL_HELMET)))
			{
				temperature.increaseTarget(addedTemp);
			}

			// Gold
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.GOLDEN_BOOTS)))
			{
				temperature.increaseTarget(addedTemp);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.GOLDEN_LEGGINGS)))
			{
				temperature.increaseTarget(addedTemp * 1.5f);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.GOLDEN_CHESTPLATE)))
			{
				temperature.increaseTarget(addedTemp * 2);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.GOLDEN_HELMET)))
			{
				temperature.increaseTarget(addedTemp);
			}

			// Iron
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.IRON_BOOTS)))
			{
				temperature.increaseTarget(addedTemp);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.IRON_LEGGINGS)))
			{
				temperature.increaseTarget(addedTemp * 1.5f);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.IRON_CHESTPLATE)))
			{
				temperature.increaseTarget(addedTemp * 2);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.IRON_HELMET)))
			{
				temperature.increaseTarget(addedTemp);
			}

			// Diamond
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.DIAMOND_BOOTS)))
			{
				temperature.increaseTarget(addedTemp);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.DIAMOND_LEGGINGS)))
			{
				temperature.increaseTarget(addedTemp * 1.5f);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.DIAMOND_CHESTPLATE)))
			{
				temperature.increaseTarget(addedTemp * 2);
			}
			else if (ItemStack.areItemsEqualIgnoreDurability(element, new ItemStack(Items.DIAMOND_HELMET)))
			{
				temperature.increaseTarget(addedTemp);
			}
		}

		// ===========================================
		// FACTORS THAT AFFECT TEMP DIRECTLY
		// ===========================================

		// Sprinting is tiring after a while. It also heats you up.
		if (player.isSprinting())
		{

			temperature.increase(0.01f);
		}

		// Being IN lava fries things pretty well.
		if (player.isInLava())
		{

			temperature.increase(0.5f);
		}

		// Being IN fire also sucks.
		if (player.isBurning())
		{

			temperature.increase(0.5f);
		}

		// Being in the water is nice.
		// This is also scaled to wetness. More wet = less heat loss, just so it
		// isn't insanely overpowered. Because water buckets are a thing.
		if (player.isInWater())
		{

			if (biomeTemp >= 1.4)
			{

				temperature.decrease(1 / ((wetness.getWetness() + 1) * 10));
			}

			else
			{

				temperature.decrease(2 / ((wetness.getWetness() + 1) * 10));
			}
		}

		// Actually affect the player's temperature. Explained in greater detail
		// at the method.
		changeRateOfTemperature(player);

		// ======================================
		// SIDE EFFECTS
		// ======================================

		// Heat stroke, hyperthermia, heat exhaustion, whatever you call it,
		// include the side effects of fatigue, nausea, and thirst.
		// The opposite (hypothermia, frostbite...) includes fatigue, shivering,
		// and more fatigue.
		// Here, it'll start off as some slowness, because being hot or cold
		// just sucks.
		// Then it'll get more and more serious.

		// Does the player have existing attributes with the same name? Remove
		// them.
		// Iterate through all of modifiers. If one of them is a temperature
		// one, delete it so another one can take its place.
		Iterator<AttributeModifier> speedModifiers = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
				.getModifiers().iterator();
		Iterator<AttributeModifier> damageModifiers = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
				.getModifiers().iterator();
		Iterator<AttributeModifier> attackSpeedModifiers = player
				.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getModifiers().iterator();

		// Speed
		while (speedModifiers.hasNext())
		{

			AttributeModifier element = speedModifiers.next();

			if (element.getName().equals("heatSpeedDebuff") || element.getName().equals("coldSpeedDebuff"))
			{

				player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(element);
			}
		}

		// Attack Damage
		while (damageModifiers.hasNext())
		{

			AttributeModifier element = damageModifiers.next();

			if (element.getName().equals("heatDamageDebuff") || element.getName().equals("coldDamageDebuff"))
			{

				player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(element);
			}
		}

		// Attack Speed
		while (attackSpeedModifiers.hasNext())
		{

			AttributeModifier element = attackSpeedModifiers.next();

			if (element.getName().equals("heatAttackSpeedDebuff") || element.getName().equals("coldAttackSpeedDebuff"))
			{

				player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).removeModifier(element);
			}
		}

		// Scale the modifiers according to current temperature.
		double speedDebuffHeatAmount = (temperature.getTemperature() - 80) * -0.002;
		double damageDebuffHeatAmount = (temperature.getTemperature() - 80) * -0.02;
		double attackSpeedDebuffHeatAmount = (temperature.getTemperature() - 80) * -0.08;

		double speedDebuffColdAmount = (30 - temperature.getTemperature()) * -0.002;
		double damageDebuffColdAmount = (30 - temperature.getTemperature()) * -0.02;
		double attackSpeedDebuffColdAmount = (30 - temperature.getTemperature()) * -0.08;

		// Create attribute modifiers
		AttributeModifier speedDebuffHot = new AttributeModifier("heatSpeedDebuff", speedDebuffHeatAmount, 0);
		AttributeModifier damageDebuffHot = new AttributeModifier("heatDamageDebuff", damageDebuffHeatAmount, 0);
		AttributeModifier attackSpeedDebuffHot = new AttributeModifier("heatAttackSpeedDebuff",
				attackSpeedDebuffHeatAmount, 0);

		AttributeModifier speedDebuffCold = new AttributeModifier("heatSpeedDebuff", speedDebuffColdAmount, 0);
		AttributeModifier damageDebuffCold = new AttributeModifier("heatDamageDebuff", damageDebuffColdAmount, 0);
		AttributeModifier attackSpeedDebuffCold = new AttributeModifier("heatAttackSpeedDebuff",
				attackSpeedDebuffColdAmount, 0);

		// Overheating (dehydration is taken care of in ThirstModifier).
		// Damage player
		if (temperature.getTemperature() > 115.0f)
		{

			player.attackEntityFrom(ModDamageSources.HYPERTHERMIA, 2.0f);
		}

		// Nausea
		if (temperature.getTemperature() > 110.0f)
		{

			SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "nausea", 100, 5, false, false);
		}

		// Fatigue
		if (temperature.getTemperature() > 100.0f)
		{

			SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "mining_fatigue", 40, 2, false, false);
		}

		// Apply attributes and sweat particles.
		if (temperature.getTemperature() > 80.0f)
		{

			player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(speedDebuffHot);
			player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(damageDebuffHot);
			player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).applyModifier(attackSpeedDebuffHot);

			SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "SweatParticles", playerPosX,
					playerPosY + 1.5, playerPosZ);

		}

		// Freezing
		// Damage player
		if (temperature.getTemperature() < -10.0f)
		{

			player.attackEntityFrom(ModDamageSources.HYPOTHERMIA, 2.0f);
		}

		// Fatigue
		if (temperature.getTemperature() < 0.0f)
		{

			SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "mining_fatigue", 40, 3, false, false);
		}

		// Apply attributes
		if (temperature.getTemperature() < 30.0f)
		{

			player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(speedDebuffCold);
			player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(damageDebuffCold);
			player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).applyModifier(attackSpeedDebuffCold);
		}
	}

	// This checks any consumed item by the player, and affects temperature
	// accordingly.
	public void onPlayerConsumeItem(EntityPlayer player, ItemStack item)
	{

		// Capability
		ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);

		// Number of items.
		int amount = item.getCount();

		// If cooked food, increase temperature.
		//TODO reimplement using list maps, i.e. mapping list of items that add the key value
		if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_CHICKEN, amount)))
		{
			temperature.increase(5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_BEEF, amount)))
		{
			temperature.increase(5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_RABBIT, amount)))
		{
			temperature.increase(5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_MUTTON, amount)))
		{
			temperature.increase(5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_PORKCHOP, amount)))
		{
			temperature.increase(5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.COOKED_FISH, amount)))
		{
			temperature.increase(2.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.BAKED_POTATO, amount)))
		{
			temperature.increase(5.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.PUMPKIN_PIE, amount)))
		{
			temperature.increase(3.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.RABBIT_STEW, amount)))
		{
			temperature.increase(10.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.MUSHROOM_STEW, amount)))
		{
			temperature.increase(10.0f);
		}
		else if (ItemStack.areItemStacksEqual(item, new ItemStack(Items.BEETROOT_SOUP, amount)))
		{
			temperature.increase(10.0f);
		}
	}

	// This is called in order to affect the rate of the player's temperature,
	// based on the target temperature.
	// The bigger the difference between the target temp and player temp, the
	// quicker the player temp changes towards the target temp, positive or
	// negative.
	private void changeRateOfTemperature(EntityPlayer player)
	{

		// Capability
		ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);

		// Difference between target temp and player temp.
		float tempDifference = temperature.getTargetTemperature() - temperature.getTemperature();

		// Rate at which the player's temp shall change. This constant might
		// change as well.
		// Changed by config
		float modifier = (float) SchopConfig.MECHANICS.temperatureScale;
		float rateOfChange = tempDifference * 0.003f * modifier;

		// Change player's temp.
		temperature.increase(rateOfChange);
	}
}