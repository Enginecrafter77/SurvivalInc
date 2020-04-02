package schoperation.schopcraft.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.schopcraft:client")
public class Client {

	@Config.LangKey("config.schopcraft:client.showCelsius")
	public boolean showCelsius = false;

	@Config.LangKey("config.schopcraft:client.showSips")
	public boolean showSipsInDurabilityBar = false;

	@Config.LangKey("config.schopcraft:client.barPos")
	public String barPositions = "middle right";
}