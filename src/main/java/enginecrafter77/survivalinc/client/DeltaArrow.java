package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;

/**
 * DeltaArrow is a little overlay element
 * designed to show a change in value of a certain
 * stat. The arrow has two modes: logarithmic and
 * linear. In logarithmic mode, the change in the
 * arrow's size becomes less noticeable for larger
 * values. This allows the user to spot fine changes
 * in the value. In linear mode, the size of the arrow
 * always reflects the real change in value. The base
 * position for the arrow is pointing up. If the value
 * change is negative, the arrow flips it's direction,
 * pointing downwards.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public class DeltaArrow extends SimpleOverlayElement {
	public static final TextureResource arrow = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/arrow.png"), 16, 24);
	
	/** The provider to use to get the stat record */
	public final StatProvider<? extends SimpleStatRecord> provider;
	
	/** Scaling operation mode. True for logarithmic, false for linear. */
	public final boolean logarithmic;
	
	/** The base multiplier applied to the value change */
	protected float amplitude;
	
	/** The minimum scale of the arrow. Beyond this value, the arrow won't shrink. */
	protected float min_scale;
	
	/** The maximum scale of the arrow. Beyond this value, the arrow won't grow. */
	protected float max_scale;
	
	public DeltaArrow(StatProvider<? extends SimpleStatRecord> provider, boolean logarithmic)
	{
		super(arrow.getSize());
		this.logarithmic = logarithmic;
		this.provider = provider;
		
		this.amplitude = 10F;
		this.min_scale = 0.3F;
		this.max_scale = 1F;
	}
	
	@Override
	public void draw(RenderFrameContext context, ReadablePoint position)
	{
		float value = 1F;

		StatTracker tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		if(tracker != null)
			value = this.getArrowScale(tracker);
		
		ReadableDimension size = this.getSize();
		
		GlStateManager.pushMatrix(); // Create new object by pushing matrix
		GlStateManager.translate(position.getX(), position.getY(), 0F); // Translate the center to position
		GlStateManager.pushMatrix(); // Create new object by pushing matrix
		GlStateManager.scale(value, value, Math.abs(1F)); // Scale the arrow
		if(value < 0F) GlStateManager.rotate(180F, 0F, 0F, 1F); // Rotate the arrow
		DeltaArrow.arrow.draw(context, new Point(-size.getWidth() / 2, -size.getHeight() / 2)); // Draw the arrow at origin point
		GlStateManager.popMatrix(); // Render the scaled and rotated arrow
		GlStateManager.popMatrix(); // Render the offset arrow in place
	}

	/**
	 * Returns the relative arrow scale form 0 (invisible)
	 * to 1 (visible). Negative returned values indicate
	 * that the arrow should be rotated upside down.
	 * @param tracker The stat tracker which tracks the provided stat
	 * @return A value from -1 to 1, ranging from 0 = invisible to |1| = fully visible
	 */
	public float getArrowScale(StatTracker tracker)
	{		
		SimpleStatRecord record = tracker.getRecord(this.provider);
		if(record == null) return 1F;
		
		float scale = Math.abs(record.getLastChange() * this.amplitude);
		if(this.logarithmic)
		{
			/*
			 * The scale is calculated using this relatively simple formula:
			 *      1 - n^(r|x * a|)
			 * y = ------------
			 *      1 - n^(r)
			 * 
			 * where n and r are constants, which define the shape of the curve, y is the
			 * resultant scale of the arrow and X is the last change to the target stat.
			 * 'a' is a constant which defines the linear scaling up of the last change,
			 * so it's real effects can be visible.
			 */
			scale = (1F - (float)Math.pow(6F, -2F * scale)) / (1F - (float)Math.pow(6F, -2F));
		}
		
		// Cap the scale
		if(scale > this.max_scale) scale = this.max_scale; // Always results in 1 or -1
		if(scale < this.min_scale && scale != 0) scale = this.min_scale;
		
		// Reintroduce the direction sign into the scale
		if(record.getLastChange() < 0) scale *= -1F;
		return scale;
	}
}
