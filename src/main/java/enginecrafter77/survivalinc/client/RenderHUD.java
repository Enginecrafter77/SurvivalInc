package enginecrafter77.survivalinc.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
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
public class RenderHUD {
	public static final RenderHUD instance = new RenderHUD(ElementType.ALL);
	
	protected final Map<OverlayElement<? super StatTracker>, ElementPositioner> elements;
	protected final Map<ElementType, Collection<ElementRenderFilter<? super StatTracker>>> filters;
	protected final ElementType trigger;
	
	public RenderHUD(ElementType trigger)
	{
		this.elements = new LinkedHashMap<OverlayElement<? super StatTracker>, ElementPositioner>();
		this.filters = new HashMap<ElementType, Collection<ElementRenderFilter<? super StatTracker>>>();
		this.trigger = trigger;
	}
	
	public void addIndependent(OverlayElement<? super StatTracker> element, ElementPositioner position)
	{
		this.elements.put(element, position);
	}
	
	public void addFilter(ElementRenderFilter<? super StatTracker> filter, ElementType element)
	{
		Collection<ElementRenderFilter<? super StatTracker>> filters = this.filters.get(element);
		if(filters == null)
		{
			filters = new LinkedList<ElementRenderFilter<? super StatTracker>>();
			this.filters.put(element, filters);
		}
		filters.add(filter);
	}
	
	public void addFilterToAll(ElementRenderFilter<? super StatTracker> filter, ElementType... elements)
	{
		for(ElementType element : elements)
		{
			this.addFilter(filter, element);
		}
	}
	
	/**
	 * @return True if registering this as event handler would have any effect, false otherwise
	 */
	public boolean isUseful()
	{
		return !this.elements.isEmpty() && !this.filters.isEmpty();
	}
	
	/**
	 * Runs the {@link ElementRenderFilter#end(ScaledResolution, Object)} for all the registered filters
	 * associated with the provided type.
	 * @param resolution The resolution the filters are running at
	 * @param element The currently processed element type
	 * @param tracker A stat tracker as argument
	 */
	private void runEndFilters(ScaledResolution resolution, ElementType element, StatTracker tracker)
	{
		Collection<ElementRenderFilter<? super StatTracker>> filters = this.filters.get(element);
		if(filters != null)
		{
			for(ElementRenderFilter<? super StatTracker> filter : filters) filter.end(resolution, tracker);
		}
	}
	
	@SubscribeEvent
	public void renderOverlayPre(RenderGameOverlayEvent.Pre event)
	{
		StatTracker tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		Collection<ElementRenderFilter<? super StatTracker>> filters = this.filters.get(event.getType());
		if(filters != null)
		{
			for(ElementRenderFilter<? super StatTracker> filter : filters)
			{
				boolean render = filter.begin(event.getResolution(), tracker);
				if(!render) event.setCanceled(true);
			}
		}
		
		/*
		 * If the event is to be cancelled, the post event will never run, thus
		 * the filters which invoke GlStateManager#pushMatrix() may leave GL in
		 * a weird state. This call makes sure that every filter that has been
		 * started will also be ended, no matter what.
		 */
		if(event.isCanceled()) this.runEndFilters(event.getResolution(), event.getType(), tracker);
	}
	
	@SubscribeEvent
	public void renderOverlayPost(RenderGameOverlayEvent.Post event)
	{
		StatTracker tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		ElementType type = event.getType();
		if(this.trigger == type)
		{
			for(Map.Entry<OverlayElement<? super StatTracker>, ElementPositioner> entry : this.elements.entrySet())
			{
				entry.getKey().draw(event.getResolution(), entry.getValue(), event.getPartialTicks(), tracker);
			}
		}
		this.runEndFilters(event.getResolution(), type, tracker);
	}
}