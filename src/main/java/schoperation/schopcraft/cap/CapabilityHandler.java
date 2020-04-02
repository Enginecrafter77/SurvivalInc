package schoperation.schopcraft.cap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import schoperation.schopcraft.SchopCraft;
import schoperation.schopcraft.cap.ghost.GhostProvider;
import schoperation.schopcraft.cap.vital.VitalStatProvider;
import schoperation.schopcraft.cap.wetness.WetnessProvider;

public class CapabilityHandler {

	/*
	 * This event handler attaches the capabilities onto the player.
	 */

	// Resource Locations
	private static final ResourceLocation WETNESS_CAP = new ResourceLocation(SchopCraft.MOD_ID, "wetness");
	private static final ResourceLocation VITAL_CAP = new ResourceLocation(SchopCraft.MOD_ID, "vitals");
	private static final ResourceLocation GHOST_CAP = new ResourceLocation(SchopCraft.MOD_ID, "ghost");

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if (!(event.getObject() instanceof EntityPlayer)) return;

		event.addCapability(WETNESS_CAP, new WetnessProvider());
		event.addCapability(VITAL_CAP, new VitalStatProvider());
		event.addCapability(GHOST_CAP, new GhostProvider());
	}
}