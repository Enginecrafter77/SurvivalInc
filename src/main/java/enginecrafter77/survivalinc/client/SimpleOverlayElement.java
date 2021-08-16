package enginecrafter77.survivalinc.client;

import org.lwjgl.util.Dimension;
import org.lwjgl.util.ReadableDimension;

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
public abstract class SimpleOverlayElement implements OverlayElement {
	
	/** The texture manager instance used to bind textures */
	public final TextureManager texturer;
	
	/** Size of the element */
	protected final ReadableDimension size;
	
	public SimpleOverlayElement(ReadableDimension size)
	{
		this.size = size;
		this.texturer = Minecraft.getMinecraft().renderEngine;
	}
	
	public SimpleOverlayElement(int width, int height)
	{
		this(new Dimension(width, height));
	}

	@Deprecated
	public int getWidth()
	{
		return this.getSize().getWidth();
	}
	
	@Deprecated
	public int getHeight()
	{
		return this.getSize().getHeight();
	}
	
	@Override
	public ReadableDimension getSize()
	{
		return this.size;
	}
}
