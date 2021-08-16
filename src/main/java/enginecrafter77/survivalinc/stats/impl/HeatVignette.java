package enginecrafter77.survivalinc.stats.impl;

import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;
import org.lwjgl.util.Rectangle;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.OverlayElement;
import enginecrafter77.survivalinc.client.TextureResource;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class HeatVignette implements OverlayElement {
	
	public static final TextureResource res = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/heatvignette.png"), 512, 256);
	public static final TextureResource cold = res.region(new Rectangle(0, 0, 256, 256)), heat = res.region(new Rectangle(256, 0, 256, 256));
	
	public final float alpha_amplitude;
	public final boolean logarithmic;
	
	public static final Range<Float> unitrange = Range.closed(0F, 1F), inertrange = Range.closed(35F, 80F);
	
	public HeatVignette(float maxalpha, boolean logarithmic)
	{
		this.alpha_amplitude = maxalpha;
		this.logarithmic = logarithmic;
	}
	
	@Override
	public void draw(ReadablePoint position, float partialTicks, Object... arguments)
	{
		StatTracker tracker = OverlayElement.getArgument(arguments, 0, StatTracker.class).orElse(Minecraft.getMinecraft().player.getCapability(StatCapability.target, null));
		SimpleStatRecord heat = tracker.getRecord(HeatModifier.instance);
		if(inertrange.contains(heat.getValue())) return;
		
		float lower = HeatVignette.getValuePropPosIn(heat.getValue(), Range.closed(heat.getValueRange().lowerEndpoint(), inertrange.lowerEndpoint()));
		float upper = HeatVignette.getValuePropPosIn(heat.getValue(), Range.closed(inertrange.upperEndpoint(), heat.getValueRange().upperEndpoint()));
		
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		if(unitrange.contains(lower)) this.drawVignette(position, partialTicks, HeatVignette.cold, 1F - lower);
		if(unitrange.contains(upper)) this.drawVignette(position, partialTicks, HeatVignette.heat, upper);
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public ReadableDimension getSize()
	{
		return cold.getSize();
	}
	
	private void drawVignette(ReadablePoint position, float partialTicks, TextureResource resource, float valueprop)
	{
		if(this.logarithmic) valueprop = (1F - (float)Math.pow(6F, -2F * valueprop)) / 0.9723F;
		
		GlStateManager.color(1F, 1F, 1F, this.alpha_amplitude * valueprop);
		resource.draw(position, partialTicks);
	}
	
	private static float getValuePropPosIn(float value, Range<Float> range)
	{
		return (value - range.lowerEndpoint()) / (range.upperEndpoint() - range.lowerEndpoint());
	}

}
