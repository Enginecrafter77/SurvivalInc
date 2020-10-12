package enginecrafter77.survivalinc.client;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface OverlayElement {
	public abstract void draw(RenderGameOverlayEvent event);
	
	public int getWidth();
	public int getHeight();
}
