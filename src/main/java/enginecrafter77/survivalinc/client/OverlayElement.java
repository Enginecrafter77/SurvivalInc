package enginecrafter77.survivalinc.client;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * OverlayElement specifies the simplest possible
 * specification of an object which can be drawn
 * on the screen as an overlay.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public interface OverlayElement<RENDER_ARGUMENT> {
	public static final Set<ElementType> ALLOW_ALL = ImmutableSet.of();
	
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, RENDER_ARGUMENT arg);
	
	public Set<ElementType> disableElements(RENDER_ARGUMENT arg);
	
	/**
	 * @return The width of the element
	 */
	public int getWidth();
	
	/**
	 * @return The height of the element
	 */
	public int getHeight();
}
