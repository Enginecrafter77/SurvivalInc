package enginecrafter77.survivalinc.client;

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
	
}
