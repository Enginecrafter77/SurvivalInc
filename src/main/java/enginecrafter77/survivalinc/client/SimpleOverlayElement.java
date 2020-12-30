package enginecrafter77.survivalinc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A simple extension to OverlayElement, SimpleOverlayElement
 * does little besides implementing the {@link #getSize(Axis2D)} method.
 * @author Enginecrafter77
 * @param <ARGUMENT>
 */
@SideOnly(Side.CLIENT)
public abstract class SimpleOverlayElement<ARGUMENT> implements OverlayElement<ARGUMENT> {
	
	/** The texture manager instance used to bind textures */
	public final TextureManager texturer;
	
	/** Height of the element */
	protected final int height;
	
	/** Width of the element */
	protected final int width;
	
	public SimpleOverlayElement(int width, int height)
	{
		this.texturer = Minecraft.getMinecraft().renderEngine;
		this.height = height;
		this.width = width;
	}

	public int getWidth()
	{
		return this.getSize(Axis2D.HORIZONTAL);
	}
	
	public int getHeight()
	{
		return this.getSize(Axis2D.VERTICAL);
	}
	
	@Override
	public int getSize(Axis2D axis)
	{
		switch(axis)
		{
		case HORIZONTAL:
			return this.width;
		case VERTICAL:
			return this.height;
		default:
			throw new UnsupportedOperationException("Axis " + axis.name() + " does not exist!");
		}
	}

}
