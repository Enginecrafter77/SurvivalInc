package enginecrafter77.survivalinc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SideOnly(Side.CLIENT)
public class SharedRenderFrameContext implements RenderFrameContext {
	protected final ReadWriteLock updateLock;

	private ScaledResolution resolution;
	private float partialTicks;

	public SharedRenderFrameContext()
	{
		this.partialTicks = 0F;
		this.resolution = null;
		this.updateLock = new ReentrantReadWriteLock();
	}

	public void updateFromEvent(RenderGameOverlayEvent event)
	{
		this.updateLock.writeLock().lock();
		this.partialTicks = event.getPartialTicks();
		this.resolution = event.getResolution();
		this.updateLock.writeLock().unlock();
	}

	public void updateFromClient(Minecraft client)
	{
		this.updateLock.writeLock().lock();
		this.partialTicks = client.getRenderPartialTicks();
		this.resolution = new ScaledResolution(client);
		this.updateLock.writeLock().unlock();
	}

	@Override
	public float getPartialTicks()
	{
		this.updateLock.readLock().lock();
		float ticks = this.partialTicks;
		this.updateLock.readLock().unlock();
		return ticks;
	}

	@Override
	public ScaledResolution getResolution()
	{
		this.updateLock.readLock().lock();
		ScaledResolution resolution = this.resolution;
		this.updateLock.readLock().unlock();
		return resolution;
	}
}
