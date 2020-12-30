package enginecrafter77.survivalinc;

import java.util.function.Predicate;

import enginecrafter77.survivalinc.client.Direction2D;
import enginecrafter77.survivalinc.client.ElementPositioner;
import enginecrafter77.survivalinc.client.HideRenderFilter;
import enginecrafter77.survivalinc.client.RenderHUD;
import enginecrafter77.survivalinc.client.StatFillBar;
import enginecrafter77.survivalinc.client.TextureResource;
import enginecrafter77.survivalinc.client.TexturedElement;
import enginecrafter77.survivalinc.client.TranslateRenderFilter;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostEnergyBar;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.ghost.RenderGhost;
import enginecrafter77.survivalinc.season.LeafColorer;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import enginecrafter77.survivalinc.stats.impl.HydrationModifier;
import enginecrafter77.survivalinc.stats.impl.SanityModifier;
import enginecrafter77.survivalinc.stats.impl.SanityRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
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
		
		TextureResource newicons = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/staticons.png"), 18, 18);
		TextureResource sanityicon = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/sanity.png"), 32, 16);
		TranslateRenderFilter moveup = new TranslateRenderFilter(new ElementPositioner(0F, 0F, 0, -10));
		if(ModConfig.HEAT.enabled)
		{
			StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(HeatModifier.instance, Direction2D.RIGHT, new TexturedElement(newicons, 0, 0, 9, 9, true));
			bar.addLayer(new TexturedElement(newicons, 9, 0, 9, 9, true), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(10);
			bar.setSpacing(-1);
			RenderHUD.instance.addIndependent(bar, new ElementPositioner(0.5F, 1F, -91, -49));
			RenderHUD.instance.addFilter(moveup, ElementType.ARMOR);
		}
		if(ModConfig.HYDRATION.enabled)
		{
			StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(HydrationModifier.instance, Direction2D.LEFT, new TexturedElement(newicons, 0, 9, 9, 9, true));
			bar.addLayer(new TexturedElement(newicons, 9, 9, 9, 9, true), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(10);
			bar.setSpacing(-1);
			RenderHUD.instance.addIndependent(bar, new ElementPositioner(0.5F, 1F, 10, -49));
			RenderHUD.instance.addFilter(moveup, ElementType.AIR);
		}
		if(ModConfig.SANITY.enabled)
		{
			StatFillBar<SanityRecord> bar = new StatFillBar<SanityRecord>(SanityModifier.instance, Direction2D.UP, new TexturedElement(sanityicon, 0, 0, 16, 16, true));
			bar.addLayer(new TexturedElement(sanityicon, 16, 0, 16, 16, true), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(1);
			RenderHUD.instance.addIndependent(bar, new ElementPositioner(0.5F, 1F, -8, -51));
			RenderHUD.instance.addFilter(moveup, ElementType.SUBTITLES);
		}
		if(ModConfig.GHOST.enabled)
		{
			Predicate<StatTracker> isGhostActive = (StatTracker tracker) -> tracker.getRecord(GhostProvider.instance).isActive();
			RenderHUD.instance.addIndependent(new GhostEnergyBar(), new ElementPositioner(0.5F, 1F, -91, -39));
			RenderHUD.instance.addFilterToAll(new HideRenderFilter<StatTracker>(isGhostActive), ElementType.HEALTH, ElementType.AIR, ElementType.ARMOR, ElementType.FOOD);
		}
		
		if(ModConfig.HEAT.enabled || ModConfig.HYDRATION.enabled) RenderHUD.instance.addFilterToAll(moveup, ElementType.CHAT);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		
		if(RenderHUD.instance.isUseful()) MinecraftForge.EVENT_BUS.register(RenderHUD.instance);
		if(ModConfig.GHOST.enabled) MinecraftForge.EVENT_BUS.register(new RenderGhost());
	}
}