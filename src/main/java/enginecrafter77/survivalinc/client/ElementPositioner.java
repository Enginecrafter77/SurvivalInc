package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * ElementPositioner describes a functional interface that
 * is used to dynamically position an element onto a screen.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface ElementPositioner {	
	public Position2D getPositionFor(ScaledResolution resolution, OverlayElement<?> element);
}
