package enginecrafter77.survivalinc.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
	
	public RenderHUD()
	{
		this.filters = new HashMap<ElementType, Collection<ElementRenderFilter<? super StatTracker>>>();
		this.elements = new LinkedList<HUDEntry>();
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
		
		for(HUDEntry entry : this.elements)
		{
			if(entry.isTrigger(event)) entry.draw(resolution, tracker, event.getPartialTicks());
		}
		
		this.runEndFilters(resolution, type, tracker);
	}
	
	public class HUDEntry
	{
		/** The rendered element itself */
		public final OverlayElement<? super StatTracker> element;
		
		/** The element positioner used to position the element on screen */
		public final ElementPositioner positioner;
		
		/**
		 * The list of the filters applies to the element.
		 * The filters are applied in an orderly fashion
		 * so that each subsequent filter is supposed to
		 * work with element filtered throught the previous
		 * filter.
		 */
		protected final List<ElementRenderFilter<? super StatTracker>> filters;
		
		/**
		 * The render trigger specifies the moment when the element
		 * is rendered. Generally speaking, the local element is
		 * rendered AFTER the triggering element was fully rendered.
		 */
		protected ElementType trigger;
		
		protected HUDEntry(OverlayElement<? super StatTracker> element, ElementPositioner positioner)
		{
			this.filters = new LinkedList<ElementRenderFilter<? super StatTracker>>();
			this.positioner = positioner;
			this.trigger = ElementType.ALL;
			this.element = element;
		}
		
		/**
		 * Sets the element render trigger.
		 * @see #trigger
		 * @param trigger The element that triggers the render of this element
		 * @return The link to the local HUD Entry for easy chaining.
		 */
		public HUDEntry setTrigger(ElementType trigger)
		{
			this.trigger = trigger;
			return this;
		}
		
		/**
		 * Attaches a {@link ElementRenderFilter} to this HUD entry.
		 * @see #filters
		 * @param filter The filter to add to the filter list
		 * @return The link to the local HUD Entry for easy chaining
		 */
		public HUDEntry addFilter(ElementRenderFilter<? super StatTracker> filter)
		{
			this.filters.add(filter);
			return this;
		}
		
		/**
		 * Tests whether the provided {@link RenderGameOverlayEvent} should trigger
		 * the render of this HUD Entry.
		 * @param event The examined event
		 * @return True if the event should trigger the rendering of this element, false otherwise.
		 */
		public boolean isTrigger(RenderGameOverlayEvent event)
		{
			return event.getType() == this.trigger;
		}
		
		/**
		 * Draws the HUD Entry and filters it though all the filters in an orderly fashion.
		 * @param resolution The resolution to draw the element in
		 * @param tracker The stat tracker
		 * @param partialTicks The partial ticks in the current render tick
		 */
		public void draw(ScaledResolution resolution, StatTracker tracker, float partialTicks)
		{
			// Due to the nature of OpenGL matrix pushing, we need to call ElementRenderFilter#begin on the last element first, so it's presumed pushMatrix runs first.
			ListIterator<ElementRenderFilter<? super StatTracker>> itr = this.filters.listIterator(this.filters.size());
			
			boolean draw = true;
			while(itr.hasPrevious())
			{
				if(!itr.previous().begin(resolution, tracker)) draw = false;
			}
			
			if(draw)
			{
				Position2D position = this.positioner.getPositionFor(resolution, this.element);
				this.element.draw(position, partialTicks, tracker);
			}
			
			// Now iterate the array back using normal order
			while(itr.hasNext())
			{
				itr.next().end(resolution, tracker);
			}
		}
	}
}