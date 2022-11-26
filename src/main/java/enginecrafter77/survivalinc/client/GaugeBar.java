package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.ReadablePoint;
import org.lwjgl.util.ReadableRectangle;
import org.lwjgl.util.Rectangle;

import java.awt.*;
import java.awt.color.ColorSpace;

@SideOnly(Side.CLIENT)
public class GaugeBar extends SimpleOverlayElement {
	private static final ReadableRectangle REGION_BACKGROUND = new Rectangle(0, 0, 8, 32);
	private static final ReadableRectangle REGION_OVERLAY = new Rectangle(8, 0, 8, 32);

	private static final TextureResource TEXTURE_RESOURCE = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/statusbar.png"), 16, 32);
	private static final TextureResource TEXTURE_BACKGROUND = TEXTURE_RESOURCE.region(REGION_BACKGROUND);
	private static final TextureResource TEXTURE_OVERLAY = TEXTURE_RESOURCE.region(REGION_OVERLAY);
	
	public final SymbolFillBar background, overlay;
	
	/** The base color */
	public final Color color;
	
	/** Individual RGB color components */
	private final float[] colorcomponents;

	public GaugeBar(Color color)
	{
		super(8, 32);
		this.background = new SymbolFillBar(TEXTURE_BACKGROUND, Direction2D.UP);
		this.overlay = new SymbolFillBar(TEXTURE_OVERLAY, Direction2D.UP);
		this.background.setCapacity(1);
		this.overlay.setCapacity(1);

		this.overlay.setFill(0.5F);

		this.color = color;
		this.colorcomponents = new float[]{0F, 0F, 0F, 1F};
		this.color.getColorComponents(ColorSpace.getInstance(ColorSpace.CS_sRGB), this.colorcomponents);
	}

	public void setFill(float fill)
	{
		this.overlay.setFill(fill);
	}
	
	@Override
	public void draw(RenderFrameContext context, ReadablePoint position)
	{
		this.background.draw(context, position);
		
		// Draw the gauge infill (colored using OpenGL)
		GlStateManager.pushMatrix();
		GlStateManager.color(this.colorcomponents[0], this.colorcomponents[1], this.colorcomponents[2], this.colorcomponents[3]);
		this.overlay.draw(context, position);
		GlStateManager.resetColor();
		GlStateManager.popMatrix();
	}
	
}
