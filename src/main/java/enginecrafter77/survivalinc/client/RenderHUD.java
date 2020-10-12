package enginecrafter77.survivalinc.client;

import java.util.LinkedList;
import java.util.List;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHUD extends OverlayElementGroup {
	public static final RenderHUD instance = new RenderHUD();
	
	public final List<OverlayElement> external;
	
	public RenderHUD()
	{
		super(Axis.HORIZONTAL);
		this.external = new LinkedList<OverlayElement>();
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		this.draw(event);
		for(OverlayElement element : this.external)
			element.draw(event);
	}
	
	@Override
	public void onResolutionChange(ScaledResolution res)
	{
		this.setPositionOrigin((float)ModConfig.CLIENT.statBarPosition[0], (float)ModConfig.CLIENT.statBarPosition[1]);
		this.setPositionOffset((int)ModConfig.CLIENT.statBarPosition[2], (int)ModConfig.CLIENT.statBarPosition[3]);
		SurvivalInc.logger.info("Resolution change detected. Changing resolution to {}x{}", res.getScaledWidth(), res.getScaledHeight());
		super.onResolutionChange(res);
	}
}