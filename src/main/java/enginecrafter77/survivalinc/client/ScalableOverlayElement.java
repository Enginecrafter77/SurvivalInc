package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ScalableOverlayElement implements OverlayElement {
	
	protected int offX, offY;
	protected float mulX, mulY;
	
	private ScaledResolution computed;
	private int posX, posY;
	
	public final int width, height;
	
	public ScalableOverlayElement(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		this.computed = null;
		this.mulX = 0F;
		this.mulY = 0F;
		this.offX = 0;
		this.offY = 0;
		this.posX = 0;
		this.posY = 0;
	}
	
	public abstract void draw();
	
	@Override
	public void draw(RenderGameOverlayEvent event)
	{
		ScaledResolution res = event.getResolution();
		if(!res.equals(this.computed))
		{
			this.onResolutionChange(res);
			this.computed = res;
		}
		
		this.draw();
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
	
	public void setAbsolutePosition(int x, int y)
	{
		this.offX = x;
		this.offY = y;
		this.markPositionForUpdate();
	}
	
	public void setRelativePositionBase(float x, float y)
	{
		this.mulX = x;
		this.mulY = y;
		this.markPositionForUpdate();
	}
	
	protected int calculateX(ScaledResolution resolution)
	{
		return Math.round((float)resolution.getScaledWidth() * this.mulX + (float)this.offX);
	}
	
	protected int calculateY(ScaledResolution resolution)
	{
		return Math.round((float)resolution.getScaledHeight() * this.mulY + (float)this.offY);
	}
	
	public int getX()
	{
		return this.posX;
	}
	
	public int getY()
	{
		return this.posY;
	}
	
	public void markPositionForUpdate()
	{
		this.computed = null;
	}
	
	public void onResolutionChange(ScaledResolution resolution)
	{
		this.posX = this.calculateX(resolution);
		this.posY = this.calculateY(resolution);
	}
}
