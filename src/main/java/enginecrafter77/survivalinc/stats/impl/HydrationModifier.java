package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.OperationType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class HydrationModifier {
	
	public static void init()
	{
		DefaultStats.HYDRATION.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.isInLava(), -0.5F), OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.dimension == -1, -0.006F), OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.world.rand.nextBoolean(), -0.003F), OperationType.OFFSET);
	}
	
	// When a player interacts with a block (usually right clicking something).
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(player.isCreative() || player.isSpectator()) return;
		
		if(player.getHeldItem(EnumHand.MAIN_HAND) == ItemStack.EMPTY)
		{
			Vec3d look = player.getPositionEyes(1.0f).add(player.getLookVec().addVector(player.getLookVec().x < 0 ? -0.5 : 0.5, -1, player.getLookVec().z < 0 ? -0.5 : 0.5));
			RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(1.0f), look, true);
			if(raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				BlockPos position = raytrace.getBlockPos();
				if(player.world.getBlockState(position).getMaterial() == Material.WATER)
				{
					if(!player.world.isRemote)
					{
						WorldServer world = (WorldServer)player.world;
						world.spawnParticle(EnumParticleTypes.WATER_SPLASH, raytrace.hitVec.x, raytrace.hitVec.y + 0.1, raytrace.hitVec.z, 20, 0.5, 0.1, 0.5, 0.1, null);
						world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, raytrace.hitVec.x, raytrace.hitVec.y + 0.1, raytrace.hitVec.z, 20, 0.5, 0.1, 0.5, 0.1, null);
						world.playSound(null, position, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.AMBIENT, 0.5f, 1.5f);
						
						StatTracker tracker = player.getCapability(StatRegister.CAPABILITY, null);
						tracker.modifyStat(DefaultStats.HYDRATION, 5F);
					}
				}
			}
		}
	}
	
}
