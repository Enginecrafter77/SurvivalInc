package enginecrafter77.survivalinc.client;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public abstract class SimpleOverlayElement<ARGUMENT> implements OverlayElement<ARGUMENT> {
	
	public final TextureManager texturer;
	
	public final int height;
	public final int width;
	
	public SimpleOverlayElement(int width, int height)
	{
		this.texturer = Minecraft.getMinecraft().renderEngine;
		this.height = height;
		this.width = width;
	}
	
	@Override
	public Set<ElementType> disableElements(ARGUMENT arg)
	{
		return OverlayElement.ALLOW_ALL;
	}

	@Override
	public int getWidth()
	{
		return this.width;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}

}
