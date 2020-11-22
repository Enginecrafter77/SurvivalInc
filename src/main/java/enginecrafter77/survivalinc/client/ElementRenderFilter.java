package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;

public interface ElementRenderFilter<ARGUMENT> {
	public boolean begin(ScaledResolution resoultion, ARGUMENT arg);
	public void end(ScaledResolution resoultion, ARGUMENT arg);
}
