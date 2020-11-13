package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class TextureResource {
	
	public final ResourceLocation texture;
	
	public final int texture_height;
	public final int texture_width;
	
	public TextureResource(ResourceLocation texture, int w, int h)
	{
		this.texture = texture;
		this.texture_width = w;
		this.texture_height = h;
	}
	
	public void load(TextureManager manager)
	{
		manager.bindTexture(this.texture);
	}
	
	public DrawableTexture createDrawable(int u, int v, int w, int h, boolean alpha)
	{
		return new DrawableTexture(u, v, w, h, alpha);
	}
	
	public class DrawableTexture extends SimpleOverlayElement<Object>
	{
		public final int offset_x;
		public final int offset_y;
		
		public final boolean hasAlpha;
		
		protected DrawableTexture(int offset_x, int offset_y, int width, int height, boolean alpha)
		{
			super(width, height);
			this.offset_x = offset_x;
			this.offset_y = offset_y;
			this.hasAlpha = alpha;
		}
		
		public void drawScaled(int x, int y, int width, int height)
		{
			Gui.drawModalRectWithCustomSizedTexture(x, y, this.offset_x, this.offset_y, width, height, texture_width, texture_height);
		}
		
		public void draw(int x, int y)
		{
			this.drawScaled(x, y, this.width, this.height);
		}
		
		@Override
		public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, Object arg)
		{
			this.draw(position.getX(resolution), position.getY(resolution));
		}
		
		public void begin(TextureManager manager)
		{
			load(manager);
			if(this.hasAlpha) GlStateManager.enableAlpha();
		}
		
		public void end()
		{
			if(this.hasAlpha) GlStateManager.disableAlpha();
		}
	}
	
}
