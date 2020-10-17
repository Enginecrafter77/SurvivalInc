package enginecrafter77.survivalinc.client;

import java.awt.Color;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimpleStatBar extends OverlayElementGroup<StatTracker> {	
	public final StatProvider provider;
	
	public SimpleStatBar(StatProvider provider, ResourceLocation icon, Color color)
	{
		super(Axis.VERTICAL);
		this.provider = provider;
		
		this.elements.add(new DifferentialArrow(provider, 8, 12));
		this.elements.add(new ElementTypeAdapter<StatTracker, Float>(new GaugeBar(color), this::getRecordValue));
		this.elements.add(new IconRender(icon, 8, 12));
		
		// Create a dummy record to see if it's a subclass of SimpleStatRecord
		if(!(provider.createNewRecord() instanceof SimpleStatRecord))
		{
			throw new IllegalArgumentException("Differential Arrow can be used only with providers using SimpleStatRecord records!");
		}
	}

	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, StatTracker tracker)
	{
		if(tracker.isActive(this.provider, Minecraft.getMinecraft().player))
		{
			super.draw(resolution, position, partialTicks, tracker);
		}
	}

	private Float getRecordValue(StatTracker tracker)
	{
		SimpleStatRecord record = (SimpleStatRecord)tracker.getRecord(provider);
		return record.getValue() / record.valuerange.upperEndpoint();
	}
	
	public static class IconRender extends SimpleOverlayElement<Object>
	{
		protected final ResourceLocation texture;
		private int texoffx, texoffy, texwidth, texheight;
		
		public IconRender(ResourceLocation texture, int width, int height)
		{
			super(width, height);
			this.texture = texture;
			this.texheight = 12;
			this.texwidth = 8;
			this.texoffx = 0;
			this.texoffy = 0;
		}
		
		public void setTextureDimensions(int x, int y, int width, int height)
		{
			this.texheight = height;
			this.texwidth = width;
			this.texoffx = x;
			this.texoffy = y;
		}

		@Override
		public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, Object arg)
		{
			GlStateManager.enableAlpha(); // Enable alpha, we will need it
			// Draw the stat icon
			this.texturer.bindTexture(this.texture);
			Gui.drawModalRectWithCustomSizedTexture(position.getX(resolution), position.getY(resolution), texoffx, texoffy, this.width, this.height, this.texwidth, this.texheight);
			GlStateManager.disableAlpha(); // Disable alpha, just in case
		}
	}
}