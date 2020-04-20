package enginecrafter77.survivalinc.client;

import java.awt.Color;
import java.util.HashSet;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.item.ItemCanteen;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHUD extends HashSet<StatRender> {
	private static final long serialVersionUID = -3636268515627373812L;

	public static final RenderHUD instance = new RenderHUD();
	
	protected StatTracker tracker;
	
	private RenderHUD()
	{
		this.tracker = null;
	}
	
	public static void register()
	{
		SimpleStatContainer stats = new SimpleStatContainer();
		stats.add(new SimpleStatBar(HeatModifier.instance, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/heat.png"), new Color(0xE80000)));
		stats.add(new SimpleStatBar(DefaultStats.HYDRATION, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/hydration.png"), new Color(ItemCanteen.waterBarColor)));
		stats.add(new SimpleStatBar(DefaultStats.SANITY, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/sanity.png"), new Color(0xF6AF25)));
		stats.add(new SimpleStatBar(DefaultStats.WETNESS, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/wetness.png"), new Color(0x0047D5)));
		RenderHUD.instance.add(stats);
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if(tracker == null)
			this.tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		
		if(event.getType() != ElementType.HOTBAR) return;
		ScaledResolution resolution = event.getResolution();
		
		for(StatRender render : this)
		{
			if(render == null)
			{
				SurvivalInc.logger.error("Null stat renderer registered inside RenderHUD. Removing to avoid future errors...");
				this.remove(render);
			}
			else render.draw(resolution, tracker);
		}
	}
}