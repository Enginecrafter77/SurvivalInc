package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface StatRender {
	public void draw(ScaledResolution resolution, StatTracker tracker);
	
	public int getDimension(Axis axis);
	public void setPosition(Axis axis, int value);
	
	public static enum Axis { HORIZONTAL, VERTICAL }
}
