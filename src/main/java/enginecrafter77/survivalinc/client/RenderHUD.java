package enginecrafter77.survivalinc.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHUD extends OverlayElementGroup<StatTracker> {
	public static final ElementPositioner origin = new ImmutableElementPosition(0F, 0F, 0, 0);
	public static final RenderHUD instance = new RenderHUD();
	
	public final List<OverlayElement<StatTracker>> external;	
	public final ElementPositioner positioner;
	protected ElementType type;
	
	public RenderHUD()
	{
		super(Axis.HORIZONTAL);
		this.external = new LinkedList<OverlayElement<StatTracker>>();
		this.positioner = new ElementPositioner();
		this.type = ElementType.ALL;
		
		this.positioner.setPositionOrigin((float)ModConfig.CLIENT.statBarPosition[0], (float)ModConfig.CLIENT.statBarPosition[1]);
		this.positioner.setPositionOffset((int)ModConfig.CLIENT.statBarPosition[2], (int)ModConfig.CLIENT.statBarPosition[3]);
	}
	
	/**
	 * @return True if registering this as event handler would have any effect, false otherwise
	 */
	public boolean isUseful()
	{
		return !(this.elements.isEmpty() && this.external.isEmpty());
	}
	
	@Override
	public Set<ElementType> disableElements(StatTracker tracker)
	{
		Set<ElementType> elements = super.disableElements(tracker);
		for(OverlayElement<StatTracker> ext : this.external)
			elements.addAll(ext.disableElements(tracker));
		return elements;
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		StatTracker tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		ElementType type = event.getType();
		if(type == this.type)
		{
			this.draw(event.getResolution(), this.positioner, event.getPartialTicks(), tracker);
			for(OverlayElement<StatTracker> ext : this.external)
				ext.draw(event.getResolution(), RenderHUD.origin, event.getPartialTicks(), tracker);
		}
		
		if(this.disableElements(tracker).contains(type)) event.setCanceled(true);
	}
}