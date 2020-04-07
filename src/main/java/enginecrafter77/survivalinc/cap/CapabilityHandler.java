package enginecrafter77.survivalinc.cap;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.cap.ghost.GhostProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler {

	/*
	 * This event handler attaches the capabilities onto the player.
	 */
	private static final ResourceLocation GHOST_CAP = new ResourceLocation(SurvivalInc.MOD_ID, "ghost");

	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if(!(event.getObject() instanceof EntityPlayer)) return;
		event.addCapability(GHOST_CAP, new GhostProvider());
	}
}