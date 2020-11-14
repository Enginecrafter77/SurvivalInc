package enginecrafter77.survivalinc;

import java.awt.Color;

import enginecrafter77.survivalinc.client.ImmutableElementPosition;
import enginecrafter77.survivalinc.client.RenderHUD;
import enginecrafter77.survivalinc.client.SimpleStatBar;
import enginecrafter77.survivalinc.client.StatFillBar;
import enginecrafter77.survivalinc.client.TextureResource;
import enginecrafter77.survivalinc.client.TexturedElement;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostEnergyBar;
import enginecrafter77.survivalinc.ghost.RenderGhost;
import enginecrafter77.survivalinc.season.LeafColorer;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import enginecrafter77.survivalinc.stats.impl.HydrationModifier;
import enginecrafter77.survivalinc.stats.impl.SanityModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		
		if(ModConfig.SEASONS.enabled) MinecraftForge.EVENT_BUS.register(LeafColorer.instance);
	}
	
	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		
		TextureResource oldicons = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/staticons.png"), 16, 24);
		TextureResource newicons = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/staticons-new.png"), 18, 18);
		if(ModConfig.HEAT.enabled) RenderHUD.instance.add(new SimpleStatBar(HeatModifier.instance, new TexturedElement(oldicons, 0, 0, 8, 12, true), new Color(0xE80000)));
		if(ModConfig.HYDRATION.enabled)
		{
			StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(HydrationModifier.instance, SimpleStatRecord.class, new TexturedElement(newicons, 0, 9, 9, 9, true), 10);
			bar.addOverlay(new TexturedElement(newicons, 9, 9, 9, 9, true), SimpleStatRecord::getNormalizedValue);
			bar.setSpacing(-1);
			RenderHUD.instance.addIndependent(bar, new ImmutableElementPosition(0.5F, 1F, -92, -49));
		}
		if(ModConfig.SANITY.enabled) RenderHUD.instance.add(new SimpleStatBar(SanityModifier.instance, new TexturedElement(oldicons, 0, 12, 8, 12, true), new Color(0xF6AF25)));
		if(ModConfig.GHOST.enabled) RenderHUD.instance.addIndependent(new GhostEnergyBar(), new ImmutableElementPosition(0.5F, 1F, -91, -39));
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		
		if(RenderHUD.instance.isUseful()) MinecraftForge.EVENT_BUS.register(RenderHUD.instance);
		if(ModConfig.GHOST.enabled) MinecraftForge.EVENT_BUS.register(new RenderGhost());
	}
}