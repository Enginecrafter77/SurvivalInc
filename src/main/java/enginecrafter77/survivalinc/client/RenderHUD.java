package enginecrafter77.survivalinc.client;

import java.util.LinkedList;
import java.util.List;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHUD extends Gui {
	
	public static final RenderHUD instance = new RenderHUD();
	
	public final List<StatBar> statbars;
	
	protected StatTracker tracker;
	
	private RenderHUD()
	{
		this.statbars = new LinkedList<StatBar>();
		this.tracker = null;
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if(tracker == null) this.tracker = Minecraft.getMinecraft().player.getCapability(StatRegister.CAPABILITY, null);
		
		if(event.getType() != ElementType.HOTBAR) return;
		ScaledResolution resolution = event.getResolution();
		
		int x = resolution.getScaledWidth() / 2 + 95;
		
		for(StatBar bar : this.statbars)
		{
			try
			{
				bar.draw(tracker, x, resolution.getScaledHeight() - bar.spacing - bar.getTotalHeight());
				x += bar.getTotalWidth() + bar.spacing;
			}
			catch(NullPointerException exc)
			{
				System.err.format("Server doesn't track stat %s! Some other mod on client's side is overriding default implementation." + bar.key.getStatID());
			}
		}
	}
}