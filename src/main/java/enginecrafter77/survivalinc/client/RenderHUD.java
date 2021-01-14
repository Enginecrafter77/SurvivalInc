package enginecrafter77.survivalinc.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
	protected final Collection<HUDEntry> elements;
	protected final Map<ElementType, Collection<ElementRenderFilter<? super StatTracker>>> filters;
	protected final ElementType trigger;
	
	public RenderHUD(ElementType trigger)
	{
		this.filters = new HashMap<ElementType, Collection<ElementRenderFilter<? super StatTracker>>>();
		this.elements = new LinkedList<HUDEntry>();
		this.trigger = trigger;
	}
	
	/**
	 * @deprecated Naming, use {@link #addElement(OverlayElement, ElementPositioner)}
	 * @param element
	 * @param position
	 */
	@Deprecated
	public void addIndependent(OverlayElement<? super StatTracker> element, ElementPositioner position)
	{
		this.addElement(element, position);
	}
	
	public HUDEntry addElement(OverlayElement<? super StatTracker> element, ElementPositioner position)
	{
		HUDEntry entry = new HUDEntry(element, position);
		this.elements.add(entry);
		return entry;
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
	
	public void reset()
	{
		this.elements.clear();
		this.filters.clear();
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
		ScaledResolution resolution = event.getResolution();
		ElementType type = event.getType();
		if(this.trigger == type)
		{
			for(HUDEntry entry : this.elements)
			{
				entry.draw(resolution, tracker, event.getPartialTicks());
			}
		}
		this.runEndFilters(resolution, type, tracker);
	}
	
	public class HUDEntry
	{
		public final OverlayElement<? super StatTracker> element;
		public final ElementPositioner positioner;
		public final List<ElementRenderFilter<? super StatTracker>> filters;
		
		protected HUDEntry(OverlayElement<? super StatTracker> element, ElementPositioner positioner)
		{
			this.filters = new LinkedList<ElementRenderFilter<? super StatTracker>>();
			this.positioner = positioner;
			this.element = element;
		}
		
		public HUDEntry addFilter(ElementRenderFilter<? super StatTracker> filter)
		{
			this.filters.add(filter);
			return this;
		}
		
		public void draw(ScaledResolution resolution, StatTracker tracker, float partialTicks)
		{
			boolean draw = true;
			for(ElementRenderFilter<? super StatTracker> filter : this.filters)
			{
				if(!filter.begin(resolution, tracker)) draw = false;
			}
			
			if(draw)
			{
				Position2D position = this.positioner.getPositionOn(resolution);
				this.element.draw(position, partialTicks, tracker);
			}
			
			filters.forEach((ElementRenderFilter<? super StatTracker> filter) -> filter.end(resolution, tracker));
		}
	}
}