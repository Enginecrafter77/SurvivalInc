package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StatBar extends Gui {
	private static final ResourceLocation bartemplate = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/statusbar.png");
	private static final int bartemplate_width = 16, bartemplate_height = 32;
	
	public final TextureManager texturer;
	public final StatProvider key;
	/** Format: ARGB */
	public float[] color;
	
	protected final ResourceLocation texture;
	protected final int texoffx, texoffy, texwidth, texheight;
	
	protected int width, barheight, iconheight, spacing;
	
	public StatBar(StatProvider key, ResourceLocation icon, int color)
	{
		this(key, icon, 0, 0, color);
	}
	
	public StatBar(StatProvider key, ResourceLocation texture, int texture_x, int texture_y, int color)
	{
		this.texturer = Minecraft.getMinecraft().renderEngine;
		this.texture = texture;
		this.texoffx = texture_x;
		this.texoffy = texture_y;
		this.color = StatBar.decodeARGB(color);
		this.key = key;
		
		this.texheight = 12;
		this.texwidth = 8;
		this.width = 8;
		this.barheight = 32;
		this.iconheight = 12;
		this.spacing = 2;
	}
	
	public void draw(StatTracker stats, int x, int y)
	{
		float prop = StatBar.bartemplate_height * this.getStatProportional(stats);
		int bar_bottom_dist = Math.round(prop), bar_top_dist = Math.round((float)bartemplate_height - prop);
		
		// Draw the icon
		GlStateManager.enableAlpha();
		this.texturer.bindTexture(texture);
		Gui.drawModalRectWithCustomSizedTexture(x, y + barheight + spacing, texoffx, texoffy, width, iconheight, texwidth, texheight);
		
		// Draw the gauge frame
		this.texturer.bindTexture(bartemplate);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, barheight, StatBar.bartemplate_width, StatBar.bartemplate_height);
		
		// Draw the gauge infill (colored using OpenGL)
		GlStateManager.pushMatrix();
		GlStateManager.color(color[1], color[2], color[3], 1F - (float)ModConfig.CLIENT.barTransparency);
		Gui.drawModalRectWithCustomSizedTexture(x, y + bar_top_dist, StatBar.bartemplate_width / 2, bar_top_dist, width, bar_bottom_dist, StatBar.bartemplate_width, StatBar.bartemplate_height);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
		// Disable alpha
		GlStateManager.disableAlpha();
	}
	
	public int getTotalHeight()
	{
		return barheight + spacing + iconheight;
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