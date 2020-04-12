package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;

import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.OperationType;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;

/*
 * This is where the magic of changing one's wetness occurs. You'll most likely be here.
 * Geez that sounds wrong. I wonder if Klei implemented their wetness mechanic with dumb jokes too.
 * This is also the first mechanic I implemented, so some old code will most likely be here.
 * The newest is temperature, so that'll look more functional.
 * 	-Schoperation
 */

@Mod.EventBusSubscriber
public class WetnessModifier {
	public static Map<Block, Float> humiditymap;
	
	public static final PlayerAttributeModifier wetness_slowdown = new PlayerAttributeModifier(DefaultStats.WETNESS, SharedMonsterAttributes.MOVEMENT_SPEED);
	
	public static void init()
	{
		DefaultStats.WETNESS.modifiers.put(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.world.isRainingAt(player.getPosition().up()), 0.01F), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.put(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.dimension == -1, -0.08F), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.put(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.isInLava(), -5F), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.put(new FunctionalModifier<EntityPlayer>(WetnessModifier::scanSurroundings), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.put(new FunctionalModifier<EntityPlayer>(WetnessModifier::naturalDrying), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.put(new FunctionalModifier<EntityPlayer>(WetnessModifier::whenInWater), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.put(new FunctionalModifier<EntityPlayer>(WetnessModifier::causeDripping), OperationType.NOOP);
		DefaultStats.WETNESS.modifiers.put(WetnessModifier.wetness_slowdown, OperationType.NOOP);
		
		WetnessModifier.humiditymap = new HashMap<Block, Float>();
		WetnessModifier.humiditymap.put(Blocks.FIRE, -0.5F);
		WetnessModifier.humiditymap.put(Blocks.LAVA, -1F);
		WetnessModifier.humiditymap.put(Blocks.FLOWING_LAVA, -1F);
		WetnessModifier.humiditymap.put(Blocks.LIT_FURNACE, -0.4F);
		WetnessModifier.humiditymap.put(Blocks.MAGMA, -0.4F);
	}
	
	public static float causeDripping(EntityPlayer player, float current)
	{
		WorldServer serverworld = (WorldServer)player.world;
		float particle_amount = 20 * (current / DefaultStats.WETNESS.getMaximum());
		serverworld.spawnParticle(EnumParticleTypes.DRIP_WATER, player.posX, player.posY, player.posZ, (int)particle_amount, 0.25, 0.5, 0.25, 0.1, null);
		return current;
	}
	
	public static float whenInWater(EntityPlayer player)
	{
		if(player.isInWater())
		{
			/*
			 * Hey now, the player could just be in one block of water at
			 * their feet. If so, just go to 40% wetness Or somewhere around there.
			 * We'll check if water is in the player's face. If so, 100%. If not, 40%.
			 */
			Block headblock = player.world.getBlockState(player.getPosition().up()).getBlock();
			if(headblock != Blocks.WATER && headblock != Blocks.FLOWING_WATER)
			{
				StatTracker stats = player.getCapability(StatRegister.CAPABILITY, null);
				if(stats.getStat(DefaultStats.WETNESS) < 40) return 1.25F;
			}
			else return 5F;
		}
		return 0F;
	}
	
	public static float naturalDrying(EntityPlayer player)
	{
		float rate = -0.005F;
		if(player.world.isDaytime() && player.world.canBlockSeeSky(player.getPosition())) rate *= 2;
		return rate;
	}
	
	public static float scanSurroundings(EntityPlayer player)
	{
		Vec3i offset = new Vec3i(2, 1, 2);
		BlockPos origin = player.getPosition();
		
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(origin.subtract(offset), origin.add(offset));
		
		float diff = 0;
		for(BlockPos position : blocks)
		{
			Block block = player.world.getBlockState(position).getBlock();
			if(WetnessModifier.humiditymap.containsKey(block))
			{
				float basewetness = WetnessModifier.humiditymap.get(block);
				float proximity = (float)Math.sqrt(origin.distanceSq(position));
				diff += basewetness / proximity;
			}
		}
		if(player.isWet()) diff /= 2F;
		return diff;
	}
}