package enginecrafter77.survivalinc.client;

import java.util.HashSet;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHUD extends HashSet<StatRender> {
	private static final long serialVersionUID = -3636268515627373812L;

	public static final RenderHUD instance = new RenderHUD();
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{		
		if(event.getType() != ElementType.HOTBAR) return;
		
		StatTracker tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		ScaledResolution resolution = event.getResolution();
		
		for(StatRender render : this)
		{
			if(render == null)
			{
				SurvivalInc.logger.error("Null stat renderer registered inside RenderHUD. Removing to avoid future errors...");
				this.remove(render);
			}
			else
			{
				render.draw(resolution, tracker);
			}
		}
	}
}