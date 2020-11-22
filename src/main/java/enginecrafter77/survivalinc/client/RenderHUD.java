package enginecrafter77.survivalinc.client;

import java.util.LinkedHashMap;
import java.util.Map;
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
	public static final ElementPositioner origin = new ElementPositioner(0F, 0F, 0, 0);
	public static final RenderHUD instance = new RenderHUD();
	
	protected final Map<OverlayElement<? super StatTracker>, ElementPositioner> external;	
	public final ElementPositioner positioner;
	protected ElementType type;
	
	public RenderHUD()
	{
		super(Axis2D.HORIZONTAL);
		this.external = new LinkedHashMap<OverlayElement<? super StatTracker>, ElementPositioner>();
		this.positioner = new ElementPositioner((float)ModConfig.CLIENT.statBarPosition[0], (float)ModConfig.CLIENT.statBarPosition[1], (int)ModConfig.CLIENT.statBarPosition[2], (int)ModConfig.CLIENT.statBarPosition[3]);
		this.type = ElementType.ALL;
	}
	
	public void addIndependent(OverlayElement<? super StatTracker> element, ElementPositioner position)
	{
		this.external.put(element, position);
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
		for(OverlayElement<? super StatTracker> ext : this.external.keySet())
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
			for(Map.Entry<OverlayElement<? super StatTracker>, ElementPositioner> entry : this.external.entrySet())
			{
				entry.getKey().draw(event.getResolution(), entry.getValue(), event.getPartialTicks(), tracker);
			}
		}
		
		if(this.disableElements(tracker).contains(type)) event.setCanceled(true);
	}
}