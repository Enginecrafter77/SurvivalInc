package enginecrafter77.survivalinc.client;

import java.awt.Color;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StatBar extends Gui {	
	public final StatProvider key;
	
	protected final GaugeBar gauge;
	protected final ResourceLocation texture;
	protected int texoffx, texoffy, texwidth, texheight;
	protected int iconheight, spacing;
	
	public StatBar(StatProvider key, ResourceLocation icon, Color color)
	{
		this(key, icon, 0, 0, color);
	}
	
	public StatBar(StatProvider key, ResourceLocation texture, int texture_x, int texture_y, Color color)
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
	}
	
	public void draw(StatTracker stats, int x, int y)
	{
		this.gauge.draw(x, y, this.getStatProportional(stats));
		
		// Draw the icon
		GlStateManager.enableAlpha();
		this.gauge.texturer.bindTexture(texture);
		Gui.drawModalRectWithCustomSizedTexture(x, y + this.gauge.height + spacing, texoffx, texoffy, this.gauge.width, iconheight, texwidth, texheight);
		GlStateManager.disableAlpha();
	}
	
	public int getTotalHeight()
	{
		return this.gauge.height + spacing + iconheight;
	}
	
	public int getTotalWidth()
	{
		return this.gauge.width;
	}
	
	public float getStatProportional(StatTracker tracker) throws NullPointerException
	{
		return (tracker.getStat(key) - key.getMinimum()) / (key.getMaximum() - key.getMinimum());
	}
	
	public static float[] decodeARGB(int rgb)
	{
		float[] argb = new float[4];
		for(int ibyte = 0; ibyte < argb.length; ibyte++)
			argb[3 - ibyte] = ((rgb >> (ibyte * 8)) & 255) / 255F;
		return argb;
	}
}