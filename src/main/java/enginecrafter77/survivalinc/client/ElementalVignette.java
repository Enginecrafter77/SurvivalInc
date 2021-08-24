package enginecrafter77.survivalinc.client;

import java.util.List;
import java.util.function.Function;

import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * ElementalVignette represents a colored vignette overlay element.
 * It's more or less just a representation of vanilla vignette
 * in OverlayElement class hierarchy... with color tinting!
 * @author Enginecrafter77
 */
public class ElementalVignette implements OverlayElement {
	
	/** The texture resource of the vanilla vignette */
	public static final TextureResource vignette = new TextureResource(new ResourceLocation("minecraft", "textures/misc/vignette.png"), 256, 256);
	
	/** The rgba color extractors from {@link ReadableColor} object. */
	public static final List<Function<ReadableColor, Integer>> rgba_extractors = ImmutableList.of(ReadableColor::getRed, ReadableColor::getGreen, ReadableColor::getBlue, ReadableColor::getAlpha);
	
	/** The maximum alpha value of the vignette. */
	public final float alpha_amplitude;
	
	/** The vignette color tint */
	private ReadableColor tint;
	
	/** The pre-calculated vignette color tint coefficients */
	private float[] color_coefficients;
	
	public ElementalVignette(float maxalpha)
	{
		this.color_coefficients = new float[4];
		this.alpha_amplitude = maxalpha;
		this.setTint(ReadableColor.WHITE);
	}
	
	/**
	 * Sets the {@link #tint color tint} of the vignette
	 * and recomputes the internal {@link #color_coefficients color coefficients}
	 * @param tint The vignette color tint
	 */
	public void setTint(ReadableColor tint)
	{
		this.tint = tint;
		
		for(int index = 0; index < ElementalVignette.rgba_extractors.size(); index++)
			this.color_coefficients[index] = (float)ElementalVignette.rgba_extractors.get(index).apply(tint) / 255F;
	}
	
	/**
	 * Retrieves the {@link #tint color tint} of the vignette.
	 * @return The vignette color tint
	 */
	public ReadableColor getTint()
	{
		return this.tint;
	}
	
	@Override
	public void draw(ReadablePoint position, float partialTicks, Object... arguments)
	{
		float prop = Math.min(1F, OverlayElement.getArgument(arguments, 0, Float.class).orElse(1F));
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(this.color_coefficients[0], this.color_coefficients[1], this.color_coefficients[2], this.color_coefficients[3] * this.alpha_amplitude * prop);
		ElementalVignette.vignette.draw(position, partialTicks);
		GlStateManager.resetColor();
	}

	@Override
	public ReadableDimension getSize()
	{
		return vignette.getSize();
	}
}
