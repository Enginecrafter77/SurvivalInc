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
public class RenderHUD extends HashSet<StatBar> {
	private static final long serialVersionUID = -3636268515627373812L;

	public static final RenderHUD instance = new RenderHUD();
	protected StatTracker tracker;
	
	private RenderHUD()
	{
		this.tracker = null;
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if(tracker == null)
			this.tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		
		if(event.getType() != ElementType.HOTBAR) return;
		ScaledResolution resolution = event.getResolution();
		
		int x = resolution.getScaledWidth() / 2 + 95;
		
		for(StatBar bar : this)
		{
			try
			{
				bar.draw(tracker, x, resolution.getScaledHeight() - bar.spacing - bar.getTotalHeight());
				x += bar.getTotalWidth() + bar.spacing;
			}
			catch(NullPointerException exc)
			{
				SurvivalInc.logger.warn(exc.getMessage());
			}
		}
	}
}