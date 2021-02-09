package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ElementPositioner {	
	public Position2D getPositionFor(ScaledResolution resolution, OverlayElement<?> element);
}
