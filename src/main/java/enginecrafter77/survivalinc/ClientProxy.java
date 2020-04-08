package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.client.ModelRegisterer;
import enginecrafter77.survivalinc.client.RenderHUD;
import enginecrafter77.survivalinc.client.StatBar;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(new ModelRegisterer());
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		RenderHUD.instance.statbars.add(new StatBar(DefaultStats.HYDRATION, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/hydration.png"), 0x23C4FF));
		RenderHUD.instance.statbars.add(new StatBar(DefaultStats.WETNESS, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/wetness.png"), 0x0047D5));
		RenderHUD.instance.statbars.add(new StatBar(DefaultStats.SANITY, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/sanity.png"), 0xF6AF25));
		RenderHUD.instance.statbars.add(new StatBar(HeatModifier.instance, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/heat.png"), 0xE80000));
		MinecraftForge.EVENT_BUS.register(RenderHUD.instance);
	}
}