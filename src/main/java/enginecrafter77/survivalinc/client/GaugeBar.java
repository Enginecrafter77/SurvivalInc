package enginecrafter77.survivalinc.client;

import java.awt.Color;
import java.awt.color.ColorSpace;

import org.lwjgl.util.ReadablePoint;
import org.lwjgl.util.Rectangle;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * GaugeBar is a simple overlay element
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public class GaugeBar extends SimpleOverlayElement {
	private static final TextureResource bartemplate = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/statusbar.png"), 16, 32);
	
	public final SymbolFillBar background, overlay;
	
	/** The base color */
	public final Color color;
	
	/** Individual RGB color components */
	private float[] colorcomponents;
	
	public GaugeBar(Color color)
	{
		super(8, 32);
		this.background = new SymbolFillBar(GaugeBar.bartemplate.region(new Rectangle(0, 0, 8, 32)), Direction2D.UP);
		this.overlay = new SymbolFillBar(GaugeBar.bartemplate.region(new Rectangle(8, 0, 8, 32)), Direction2D.UP);
		this.background.setCapacity(1);
		this.overlay.setCapacity(1);
		
		this.color = color;
		this.colorcomponents = new float[]{0F, 0F, 0F, 1F};
		this.color.getColorComponents(ColorSpace.getInstance(ColorSpace.CS_sRGB), this.colorcomponents);
	}
	
	@Override
	public void draw(ReadablePoint position, float partialTicks, Object... args)
	{
		this.background.draw(position, partialTicks, 1F);
		
		// Draw the gauge infill (colored using OpenGL)
		GlStateManager.pushMatrix();
		GlStateManager.color(this.colorcomponents[0], this.colorcomponents[1], this.colorcomponents[2], this.colorcomponents[3]);
		this.overlay.draw(position, partialTicks, args);
		GlStateManager.resetColor();
		GlStateManager.popMatrix();
	}
	
}
