package enginecrafter77.survivalinc.client;

import java.awt.Color;

import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimpleStatBar extends GaugeBar implements StatBar {
	public final StatProvider key;
	
	protected final DifferentialArrow arrow;
	
	protected final ResourceLocation texture;
	protected int texoffx, texoffy, texwidth, texheight;
	protected int iconheight, spacing;
	
	public SimpleStatBar(StatProvider key, ResourceLocation icon, Color color)
	{
		this(key, icon, 0, 0, color);
	}
	
	public SimpleStatBar(StatProvider key, ResourceLocation texture, int texture_x, int texture_y, Color color)
	{
		super(8, 32, color);
		
		this.arrow = new DifferentialArrow(key, 8, 12);
		this.texture = texture;
		this.texoffx = texture_x;
		this.texoffy = texture_y;
		this.key = key;
		
		this.texheight = 12;
		this.texwidth = 8;
		this.iconheight = 12;
		this.spacing = 2;
		
		// Create a dummy record to see if it's a subclass of SimpleStatRecord
		if(!(key.createNewRecord() instanceof SimpleStatRecord))
		{
			throw new IllegalArgumentException("Differential Arrow can be used only with providers using SimpleStatRecord records!");
		}
	}
	
	public boolean isVisible(StatTracker tracker)
	{
		return true;
	}
	
	@Override
	protected float getProportion(StatTracker tracker)
	{
		SimpleStatRecord record = (SimpleStatRecord)tracker.getRecord(this.key);
		return (record.getValue() - record.valuerange.lowerEndpoint()) / (record.valuerange.upperEndpoint() - record.valuerange.lowerEndpoint());
	}
	
	@Override
	public void draw(ScaledResolution resolution, StatTracker stats) throws UnsupportedOperationException
	{
		if(!this.isVisible(stats)) return;
		
		super.draw(resolution, stats);
		
		GlStateManager.enableAlpha(); // Enable alpha, we will need it
		// Draw the stat icon
		this.texturer.bindTexture(texture);
		Gui.drawModalRectWithCustomSizedTexture(this.position.get(Axis.HORIZONTAL), this.position.get(Axis.VERTICAL) + this.height + spacing, texoffx, texoffy, this.width, iconheight, texwidth, texheight);
		// Draw the arrow indicating value
		this.arrow.draw(resolution, stats);
		GlStateManager.disableAlpha(); // Disable alpha, just in case
	}
	
	@Override
	public void setPosition(Axis axis, int position)
	{
		this.position.put(axis, position);
		
		if(axis == Axis.VERTICAL)
			position -= (this.arrow.height + this.spacing);
		
		this.arrow.setPosition(axis, position);
	}

	@Override
	public StatProvider getProvider()
	{
		return this.key;
	}

	@Override
	public Axis getMajorAxis()
	{
		return Axis.VERTICAL;
	}

	@Override
	public int getDimension(Axis axis)
	{
		int value = super.getDimension(axis);
		if(axis == Axis.VERTICAL)
			value += spacing + iconheight;
		return value;
	}
}