package enginecrafter77.survivalinc.client;

import java.awt.Color;
import java.util.EnumMap;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimpleStatBar extends Gui implements StatBar {	
	public final StatProvider key;
	
	protected final GaugeBar gauge;
	protected final ResourceLocation texture;
	protected int texoffx, texoffy, texwidth, texheight;
	protected int iconheight, spacing;
	
	public EnumMap<Axis, Integer> position;
	
	public SimpleStatBar(StatProvider key, ResourceLocation icon, Color color)
	{
		this(key, icon, 0, 0, color);
	}
	
	public SimpleStatBar(StatProvider key, ResourceLocation texture, int texture_x, int texture_y, Color color)
	{
		this.gauge = new GaugeBar(8, 32, color);
		this.texture = texture;
		this.texoffx = texture_x;
		this.texoffy = texture_y;
		this.key = key;
		
		this.texheight = 12;
		this.texwidth = 8;
		this.iconheight = 12;
		this.spacing = 2;
		
		this.position = new EnumMap<Axis, Integer>(Axis.class);
	}
	
	@Override
	public void draw(ScaledResolution resolution, StatTracker stats) throws UnsupportedOperationException
	{
		try
		{
			this.gauge.draw(this.position.get(Axis.HORIZONTAL), this.position.get(Axis.VERTICAL), this.getStatProportional(stats));
		}
		catch(NullPointerException exc)
		{
			UnsupportedOperationException nexc = new UnsupportedOperationException("Server doesn't track stat " + key.getStatID() + ". Some other mod on client's side is overriding default implementation.");
			nexc.initCause(exc);
			throw nexc;
		}
		
		// Draw the icon
		GlStateManager.enableAlpha();
		this.gauge.texturer.bindTexture(texture);
		Gui.drawModalRectWithCustomSizedTexture(this.position.get(Axis.HORIZONTAL), this.position.get(Axis.VERTICAL) + this.gauge.height + spacing, texoffx, texoffy, this.gauge.width, iconheight, texwidth, texheight);
		GlStateManager.disableAlpha();
	}
	
	@Override
	public void setPosition(Axis axis, int position)
	{
		this.position.put(axis, position);
	}
	
	public float getStatProportional(StatTracker tracker) throws NullPointerException
	{
		return (tracker.getStat(key) - key.getMinimum()) / (key.getMaximum() - key.getMinimum());
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
		switch(axis)
		{
		case HORIZONTAL:
			return this.gauge.width;
		case VERTICAL:
			return this.gauge.height + spacing + iconheight;
		default:
			return 0;
		}
	}
}