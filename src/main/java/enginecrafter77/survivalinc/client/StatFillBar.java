package enginecrafter77.survivalinc.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
public class StatFillBar<RECORD extends StatRecord> implements OverlayElement<StatTracker> {
	
	/** The background fill bar. */
	public final SymbolFillBar background;
	
	/** The stat provider to get the data from */
	public final StatProvider<RECORD> provider;
	
	/** The layer map */
	protected final Map<SymbolFillBar, Function<RECORD, Float>> layers;
	
	/**
	 * @param provider The provider to get the value from
	 * @param direction The direction of drawing
	 * @param base The icon
	 */
	public StatFillBar(StatProvider<RECORD> provider, Direction2D direction, TexturedElement base)
	{
		this.layers = new LinkedHashMap<SymbolFillBar, Function<RECORD, Float>>();
		this.background = new SymbolFillBar(base, direction);
		this.provider = provider;
	}
	
	/**
	 * @param provider
	 * @param recordclass
	 * @param direction
	 * @param base
	 * @deprecated Use {@link #StatFillBar(StatProvider, Direction2D, TexturedElement)} instead
	 */
	@Deprecated
	public StatFillBar(StatProvider<RECORD> provider, Class<RECORD> recordclass, Direction2D direction, TexturedElement base)
	{
		this(provider, direction, base);
	}
	
	/**
	 * @see SymbolFillBar#setSpacing(int) 
	 * @param spacing The spacing between elements
	 */
	public void setSpacing(int spacing)
	{
		this.background.setSpacing(spacing);
		for(SymbolFillBar bar : this.layers.keySet()) bar.setSpacing(spacing);
	}
	
	/**
	 * @see SymbolFillBar#setCapacity(int)
	 * @param spacing The spacing between elements
	 */
	public void setCapacity(int capacity)
	{
		this.background.setCapacity(capacity);
		for(SymbolFillBar bar : this.layers.keySet()) bar.setCapacity(capacity);
	}
	
	/**
	 * Adds a new layer with the specified symbol texture and a getter function.
	 * @param texture The symbol texture
	 * @param getter A function to get the appropriate fill ratio from the record
	 */
	public void addLayer(TexturedElement texture, Function<RECORD, Float> getter)
	{
		SymbolFillBar bar = new SymbolFillBar(texture, this.background.direction);
		bar.setCapacity(this.background.getCapacity());
		bar.setSpacing(this.background.getSpacing());
		this.layers.put(bar, getter);
	}
	
	@Override
	public int getSize(Axis2D axis)
	{
		return this.background.getSize(axis);
	}
	
	@Override
	public void draw(Position2D position, float partialTicks, StatTracker arg)
	{
		if(arg.isActive(this.provider, Minecraft.getMinecraft().player))
		{
			this.background.draw(position, partialTicks, 1F);
			
			RECORD record = arg.getRecord(this.provider);
			for(Map.Entry<SymbolFillBar, Function<RECORD, Float>> entry : this.layers.entrySet())
			{
				Function<RECORD, Float> transformer = entry.getValue();
				Float value = transformer.apply(record);
				if(value != null) entry.getKey().draw(position, partialTicks, value);
			}
		}
	}
	
}
