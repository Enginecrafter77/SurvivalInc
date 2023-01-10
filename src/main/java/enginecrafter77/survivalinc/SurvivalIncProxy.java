package enginecrafter77.survivalinc;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import javax.annotation.Nullable;

public interface SurvivalIncProxy {
	public abstract void registerNetworkHandlers(SimpleNetworkWrapper net);

	public abstract void registerRendering();

	public abstract void createHUD();

	@Nullable
	public Object getAuxiliaryEventHandler();
}
