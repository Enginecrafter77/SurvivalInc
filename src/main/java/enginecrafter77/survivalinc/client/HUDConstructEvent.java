package enginecrafter77.survivalinc.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.lwjgl.util.ReadablePoint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * HUDConstructEvent is called when the HUD is about to get constructed.
 * This event can be used to dynamically add elements and/or render filters
 * to the HUD about to be created using {@link #addElement(OverlayElement, ElementPositioner)}
 * and {@link #addRenderStageFilter(ElementRenderFilter, ElementType...)} respectively.
 * The event is also cancelable. Canceling the event causes the event result to
 * be disregarded, effectively making all the calls to addElement and addRenderStageFilter
 * ineffective.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public class HUDConstructEvent extends Event {
	protected final Collection<HUDElement> elements;
	protected final Map<ElementType, Collection<ElementRenderFilter<? super StatTracker>>> filters;
	
	public HUDConstructEvent()
	{
		this.filters = new HashMap<ElementType, Collection<ElementRenderFilter<? super StatTracker>>>();
		this.elements = new LinkedList<HUDElement>();
	}
	
	@Override
	public boolean isCancelable()
	{
		return true;
	}

	/**
	 * Adds a new element to the constructed HUD.
	 * @param element The element
	 * @param position The {@link ElementPositioner positioner} of the element
	 * @return An instance of {@link HUDElement}, which can be used in a builder-like manner.
	 */
	public HUDElement addElement(OverlayElement<? super StatTracker> element, ElementPositioner position)
	{
		HUDElement entry = new HUDElement(element, position);
		this.elements.add(entry);
		return entry;
	}
	
	/**
	 * Adds a {@link ElementRenderFilter} to the specified {@link ElementType}(s)
	 * @param filter The render filter
	 * @param elements The native elements to apply the filter to.
	 */
	public void addRenderStageFilter(ElementRenderFilter<? super StatTracker> filter, ElementType... elements)
	{
		for(ElementType element : elements)
		{
			Collection<ElementRenderFilter<? super StatTracker>> filters = this.filters.get(element);
			if(filters == null)
			{
				filters = new LinkedList<ElementRenderFilter<? super StatTracker>>();
				this.filters.put(element, filters);
			}
			filters.add(filter);
		}
	}
	
	/**
	 * A final step of {@link HUDConstructEvent}. Builds a {@link RenderHUD}
	 * implementation from the previously registered elements and render stage
	 * filters.
	 * @return A new instance of {@link RenderHUD} default implementation ({@link RenderHUDImpl})
	 */
	public RenderHUD buildHUD()
	{
		return new RenderHUDImpl(this.elements, this.filters);
	}
	
	/**
	 * HUDElement (previously HUDEntry) is a class used to associate
	 * {@link OverlayElement} with it's {@link ElementPositioner} and
	 * it's registered {@link ElementRenderFilter}s. HUDElement is basically
	 * a representation of {@link OverlayElement} that is being rendered inside
	 * a {@link RenderHUD} implementation.
	 * @author Enginecrafter77
	 */
	public static class HUDElement
	{
		/** The rendered element itself */
		public final OverlayElement<? super StatTracker> element;
		
		/** The element positioner used to position the element on screen */
		public final ElementPositioner positioner;
		
		/**
		 * The list of the filters applies to the element.
		 * The filters are applied in an orderly fashion
		 * so that each subsequent filter is supposed to
		 * work with element filtered through the previous
		 * filter.
		 */
		protected final List<ElementRenderFilter<? super StatTracker>> filters;
		
		/**
		 * The render trigger specifies the moment when the element
		 * is rendered. Generally speaking, the local element is
		 * rendered AFTER the triggering element was fully rendered.
		 */
		protected ElementType trigger;
		
		protected HUDElement(OverlayElement<? super StatTracker> element, ElementPositioner positioner)
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
		public HUDElement setTrigger(ElementType trigger)
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
		public HUDElement addFilter(ElementRenderFilter<? super StatTracker> filter)
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
				ReadablePoint position = this.positioner.getPositionFor(resolution, this.element);
				this.element.draw(position, partialTicks, tracker);
			}
			
			// Now iterate the array back using normal order
			while(itr.hasNext())
			{
				itr.next().end(resolution, tracker);
			}
		}
	}
	
	/**
	 * RenderHUD is an abstract interface which is supposed to render
	 * content on the screen on either the {@link RenderGameOverlayEvent.Pre}
	 * or {@link RenderGameOverlayEvent.Post}. 
	 * @author Enginecrafter77
	 */
	public static interface RenderHUD {
		public void renderOverlayPre(RenderGameOverlayEvent.Pre event);
		public void renderOverlayPost(RenderGameOverlayEvent.Post event);
	}

	/**
	 * The default implementation of {@link RenderHUD}.
	 * @author Enginecrafter77
	 */
	private static class RenderHUDImpl implements RenderHUD {
		protected final Collection<HUDElement> elements;
		protected final Map<ElementType, Collection<ElementRenderFilter<? super StatTracker>>> filters;
		
		private RenderHUDImpl(Collection<HUDElement> elements, Map<ElementType, Collection<ElementRenderFilter<? super StatTracker>>> filters)
		{
			this.filters = ImmutableMap.copyOf(filters);
			this.elements = ImmutableList.copyOf(elements);
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
		
		@Override
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
		
		@Override
		public void renderOverlayPost(RenderGameOverlayEvent.Post event)
		{
			StatTracker tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
			ScaledResolution resolution = event.getResolution();
			ElementType type = event.getType();
			
			for(HUDElement entry : this.elements)
			{
				if(entry.isTrigger(event)) entry.draw(resolution, tracker, event.getPartialTicks());
			}
			
			this.runEndFilters(resolution, type, tracker);
		}	
	}
}
