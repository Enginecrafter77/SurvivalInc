package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.client.RenderHUD;
import enginecrafter77.survivalinc.client.StatBar;
import enginecrafter77.survivalinc.item.ItemCanteen;
import enginecrafter77.survivalinc.net.StatUpdateMessage;
import enginecrafter77.survivalinc.net.SummonInfoPacket;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		//MinecraftForge.EVENT_BUS.register(new ModelRegisterer());
	}
	
	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		//All packets are by default sent with discriminator 0.
		//TODO Extend SNW and add handler for disc 0 on client side only to forward unregistered.
		this.net.registerMessage(RenderHUD.instance, StatUpdateMessage.class, 0, Side.CLIENT);
		this.net.registerMessage(SummonInfoPacket.class, SummonInfoPacket.SummonInfoMessage.class, 2, Side.CLIENT);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		RenderHUD.instance.statbars.add(new StatBar(DefaultStats.HYDRATION, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/hydration.png"), ItemCanteen.waterBarColor));
		RenderHUD.instance.statbars.add(new StatBar(DefaultStats.WETNESS, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/wetness.png"), 0x0047D5));
		RenderHUD.instance.statbars.add(new StatBar(DefaultStats.SANITY, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/sanity.png"), 0xF6AF25));
		RenderHUD.instance.statbars.add(new StatBar(HeatModifier.instance, new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/heat.png"), 0xE80000));
		MinecraftForge.EVENT_BUS.register(RenderHUD.instance);
	}
}