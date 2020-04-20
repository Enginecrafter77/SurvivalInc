package enginecrafter77.survivalinc;

import java.awt.Color;

import enginecrafter77.survivalinc.client.RenderHUD;
import enginecrafter77.survivalinc.client.StatBar;
import enginecrafter77.survivalinc.item.ItemCanteen;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		RenderHUD.instance.add(new StatBar(DefaultStats.HYDRATION, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/hydration.png"), new Color(ItemCanteen.waterBarColor)));
		RenderHUD.instance.add(new StatBar(DefaultStats.WETNESS, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/wetness.png"), new Color(0x0047D5)));
		RenderHUD.instance.add(new StatBar(DefaultStats.SANITY, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/sanity.png"), new Color(0xF6AF25)));
		RenderHUD.instance.add(new StatBar(HeatModifier.instance, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/heat.png"), new Color(0xE80000)));
		MinecraftForge.EVENT_BUS.register(RenderHUD.instance);
	}
}