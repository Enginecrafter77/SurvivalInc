package schoperation.schopcraft.cap.thirst;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeBeach;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.biome.BiomeSwamp;
import schoperation.schopcraft.cap.temperature.ITemperature;
import schoperation.schopcraft.cap.temperature.TemperatureProvider;
import schoperation.schopcraft.config.SchopConfig;
import schoperation.schopcraft.lib.ModDamageSources;
import schoperation.schopcraft.util.SchopServerEffects;
import schoperation.schopcraft.util.SchopServerParticles;
import schoperation.schopcraft.util.SchopServerSounds;

import java.util.Iterator;

/*
 * Where thirst is modified.
 */

public class ThirstModifier {
	
	public void onPlayerUpdate(EntityPlayer player) {
		
		// Capabilities
		IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
		ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
			
        // Modifier from config
        float modifier = (float) SchopConfig.MECHANICS.thirstScale;

        // Lava fries you well. Better grab a water.
        if (player.isInLava()) {

            thirst.decrease(0.5f);
        }

        // The nether is also good at frying.
        else if (player.dimension == -1) {

            thirst.decrease(0.006f * modifier);
        }

        // Overheating dehydrates very well.
        else if (temperature.getTemperature() > 90.0f) {

            float amountOfDehydration = temperature.getTemperature() / 10000;
            thirst.decrease(amountOfDehydration);
        }

        // Natural dehydration. "Slow" is an understatement here.
        else {

            thirst.decrease(0.003f * modifier);
        }

        // =========================================================================================================
        //                                    The side effects of thirst.
        // Side effects of dehydration include fatigue and dizzyness. Those are replicated here. Well, attempted.
        // =========================================================================================================

        // Does the player have existing attributes with the same name? Remove them.
        // Iterate through all of modifiers. If one of them is a thirst one, delete it so another one can take its place.
        Iterator<AttributeModifier> speedModifiers = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifiers().iterator();
        Iterator<AttributeModifier> damageModifiers = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getModifiers().iterator();
        Iterator<AttributeModifier> attackSpeedModifiers = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getModifiers().iterator();

        // Speed
        while (speedModifiers.hasNext()) {

            AttributeModifier element = speedModifiers.next();

            if (element.getName().equals("thirstSpeedDebuff")) {

                player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(element);
            }
        }

        // Attack Damage
        while (damageModifiers.hasNext()) {

            AttributeModifier element = damageModifiers.next();

            if (element.getName().equals("thirstDamageDebuff")) {

                player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(element);
            }
        }

        // Attack Speed
        while (attackSpeedModifiers.hasNext()) {

            AttributeModifier element = attackSpeedModifiers.next();

            if (element.getName().equals("thirstAttackSpeedDebuff")) {

                player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).removeModifier(element);
            }
        }

        // Scale the modifiers according to current thirst.
        double speedDebuffAmount = (40 - thirst.getThirst()) * -0.002;
        double damageDebuffAmount = (40 - thirst.getThirst()) * -0.02;
        double attackSpeedDebuffAmount = (40 - thirst.getThirst()) * -0.08;

        // Create attribute modifiers
        AttributeModifier speedDebuff = new AttributeModifier("thirstSpeedDebuff", speedDebuffAmount, 0);
        AttributeModifier damageDebuff = new AttributeModifier("thirstDamageDebuff", damageDebuffAmount, 0);
        AttributeModifier attackSpeedDebuff = new AttributeModifier("thirstAttackSpeedDebuff", attackSpeedDebuffAmount, 0);

        // Now determine when to debuff the player
        if (thirst.getThirst() < 5.0f) {

            player.attackEntityFrom(ModDamageSources.DEHYDRATION, 4.0f);
        }

        if (thirst.getThirst() < 15.0f) {

            SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "nausea", 100, 5, false, false);
            SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "mining_fatigue", 100, 3, false, false);
        }

        if (thirst.getThirst() < 40.0f) {

            // Speed + damage + attack speed oh my!
            player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(speedDebuff);
            player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(damageDebuff);
            player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).applyModifier(attackSpeedDebuff);
        }
	}
	
	public void onPlayerInteract(EntityPlayer player) {
		
		// Capability
		IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);

        // Ray trace result for drinking with bare hands. pretty ineffective.
        // First, some "boosts" to the vector.
        double vecX = 0;
        double vecZ = 0;
        if (player.getLookVec().x < 0) { vecX = -0.5; }
        else if (player.getLookVec().x > 0) { vecX = 0.5; }
        if (player.getLookVec().z < 0) { vecZ = -0.5; }
        else if (player.getLookVec().z > 0) { vecZ = 0.5; }

        // Now the actual raytrace.
        RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(1.0f), player.getPositionEyes(1.0f).add(player.getLookVec().addVector(vecX, -1, vecZ)), true);

        // Is there something?
        if (raytrace != null) {

            // Is it a block?
            if (raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {

                BlockPos pos = raytrace.getBlockPos();
                Iterator<ItemStack> handItems = player.getHeldEquipment().iterator();

                // If it is water and the player isn't holding jack squat (main hand).
                if (player.world.getBlockState(pos).getMaterial() == Material.WATER && handItems.next().isEmpty()) {

                    // Still more if statements. now see what biome the player is in, and quench thirst accordingly.
                    Biome biome = player.world.getBiome(pos);

                    if (biome instanceof BiomeOcean || biome instanceof BiomeBeach) {

                        thirst.decrease(0.5f);
                    }
                    else if (biome instanceof BiomeSwamp) {

                        thirst.increase(0.25f);
                        SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "poison", 12, 3, false, false);
                    }
                    else {

                        thirst.increase(0.25f);

                        // Random chance to damage player
                        double randomNum = Math.random();
                        if (randomNum <= 0.50) { // 50% chance

                            SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "poison", 12, 1, false, false);
                        }
                    }

                    // Spawn particles and sounds for drinking water
                    SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
                    SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
	}
}