package enginecrafter77.survivalinc.client;

import com.google.common.collect.ImmutableMap;

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
	protected ImmutableMap<Axis2D, Integer> size;
	
	public SimpleOverlayElement(int width, int height)
	{
		this.size = ImmutableMap.of(Axis2D.HORIZONTAL, width, Axis2D.VERTICAL, height);
		this.texturer = Minecraft.getMinecraft().renderEngine;
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
		return this.size.get(axis);
	}

}
