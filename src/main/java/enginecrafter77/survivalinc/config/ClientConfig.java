package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config.LangKey("config.survivalinc:client")
@SideOnly(Side.CLIENT)
public class ClientConfig {
	@Config.LangKey("config.survivalinc:client.barTransparency")
	@Config.RangeDouble(min = 0, max = 1)
	public double barTransparency = 0;
}