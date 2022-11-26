package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * StatFillBar presents simple interface for creating stat displays
 * similar in look to vanilla minecraft. StatFillBar is bound to
 * a certain {@link StatProvider}. Each tick, an appropriate record
 * is loaded and the base texture is drawn. Then, there are the so-called
 * layers. Each layer is assigned a function, which extracts exactly one
 * float value between 0 and 1 from the record. This value is used to draw
 * symbols spanning across the base symbols with the appropriate fill ratio.
 * @author Enginecrafter77
 * @param <RECORD> The record class
 */
@SideOnly(Side.CLIENT)
public class StatFillBar<RECORD extends StatRecord> implements OverlayElement {
	
	/** The background fill bar. */
	public final SymbolFillBar background;
	
	/** The stat provider to get the data from */
	public final StatProvider<RECORD> provider;
	
	/** The layer map */
	protected final List<StatBarRenderLayer<RECORD>> layers;
	
	/**
	 * @param provider The provider to get the value from
	 * @param direction The direction of drawing
	 * @param base The icon
	 */
	public StatFillBar(StatProvider<RECORD> provider, Direction2D direction, TextureResource base)
	{
		this.layers = new LinkedList<StatBarRenderLayer<RECORD>>();
		this.background = new SymbolFillBar(base, direction);
		this.provider = provider;
	}

	/**
	 * @see SymbolFillBar#setSpacing(int) 
	 * @param spacing The spacing between elements
	 */
	public void setSpacing(int spacing)
	{
		this.background.setSpacing(spacing);
		for(SymbolFillBar bar : this.layers) bar.setSpacing(spacing);
	}

	public void setCapacity(int capacity)
	{
		this.background.setCapacity(capacity);
		for(SymbolFillBar bar : this.layers) bar.setCapacity(capacity);
	}
	
	/**
	 * Adds a new layer with the specified symbol texture and a getter function.
	 * @param texture The symbol texture
	 * @param getter A function to get the appropriate fill ratio from the record
	 */
	public void addLayer(TextureResource texture, Function<RECORD, Float> getter)
	{
		StatBarRenderLayer<RECORD> bar = new StatBarRenderLayer<RECORD>(texture, this.background.direction, getter);
		bar.setCapacity(this.background.getCapacity());
		bar.setSpacing(this.background.getSpacing());
		this.layers.add(bar);
	}
	
	@Override
	public ReadableDimension getSize()
	{
		return this.background.getSize();
	}
	
	@Override
	public void draw(RenderFrameContext context, ReadablePoint position)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		if(tracker != null && tracker.isActive(this.provider, player))
		{
			this.background.draw(context, position);

			RECORD record = tracker.getRecord(this.provider);
			for(StatBarRenderLayer<RECORD> layer : this.layers)
			{
				layer.updateFill(record);
				layer.draw(context, position);
			}
		}
	}

	private static class StatBarRenderLayer<RECORD extends StatRecord> extends SymbolFillBar
	{
		private final Function<RECORD, Float> extractor;

		public StatBarRenderLayer(TextureResource symbol, Direction2D direction, Function<RECORD, Float> extractor)
		{
			super(symbol, direction);
			this.extractor = extractor;
		}

		public void updateFill(RECORD record)
		{
			this.setFill(this.extractor.apply(record));
		}
	}
	
}
