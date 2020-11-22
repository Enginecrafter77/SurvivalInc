package enginecrafter77.survivalinc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;

public abstract class SimpleOverlayElement<ARGUMENT> implements OverlayElement<ARGUMENT> {
	
	public final TextureManager texturer;
	
	protected final int height;
	protected final int width;
	
	public SimpleOverlayElement(int width, int height)
	{
		this.texturer = Minecraft.getMinecraft().renderEngine;
		this.height = height;
		this.width = width;
	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}
	
	public int getSize(Axis2D axis)
	{
		switch(axis)
		{
		case HORIZONTAL:
			return this.getWidth();
		case VERTICAL:
			return this.getHeight();
		default:
			return 0;
		}
	}

}
