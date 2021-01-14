package enginecrafter77.survivalinc;

import java.util.function.Predicate;

import enginecrafter77.survivalinc.client.Direction2D;
import enginecrafter77.survivalinc.client.ElementPositioner;
import enginecrafter77.survivalinc.client.HideRenderFilter;
import enginecrafter77.survivalinc.client.Position2D;
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
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
	public RenderHUD hud;
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		this.hud = new RenderHUD(ModConfig.CLIENT.hud.renderTrigger);
		if(ModConfig.SEASONS.enabled) MinecraftForge.EVENT_BUS.register(LeafColorer.instance);
	}
	
	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		this.constructHUD();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		
		if(this.hud.isUseful())
		{
			MinecraftForge.EVENT_BUS.register(this.hud);
			MinecraftForge.EVENT_BUS.register(this); // For config reloading
		}
		
		if(ModConfig.GHOST.enabled) MinecraftForge.EVENT_BUS.register(new RenderGhost());
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event)
	{
		if(!event.getModID().equals(SurvivalInc.MOD_ID)) return;
		
		String config = event.getConfigID();
		SurvivalInc.logger.info("ConfigChangedEvent({})", config);
		
		this.hud.reset();
		this.constructHUD();
	}
	
	private void constructHUD()
	{		
		float origin_x = (float)ModConfig.CLIENT.hud.originX;
		float origin_y = (float)ModConfig.CLIENT.hud.originY;
		
		TextureResource newicons = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/staticons.png"), 18, 18);
		TextureResource sanityicon = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/sanity.png"), 32, 16);
		TranslateRenderFilter moveup = new TranslateRenderFilter(new Position2D(0, -10));
		if(ModConfig.HEAT.enabled)
		{
			StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(HeatModifier.instance, ModConfig.CLIENT.hud.heatBarDirection, new TexturedElement(newicons, 0, 0, 9, 9, true));
			bar.addLayer(new TexturedElement(newicons, 9, 0, 9, 9, true), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(ModConfig.CLIENT.hud.heatBarCapacity);
			bar.setSpacing(ModConfig.CLIENT.hud.heatBarSpacing);
			hud.addElement(bar, new ElementPositioner(origin_x, origin_y, ModConfig.CLIENT.hud.heatBarX, ModConfig.CLIENT.hud.heatBarY));
			
			if(ModConfig.CLIENT.hud.moveArmorBar) hud.addFilter(moveup, ElementType.ARMOR);
		}
		if(ModConfig.HYDRATION.enabled)
		{
			StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(HydrationModifier.instance, ModConfig.CLIENT.hud.hydrationBarDirection, new TexturedElement(newicons, 0, 9, 9, 9, true));
			bar.addLayer(new TexturedElement(newicons, 9, 9, 9, 9, true), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(ModConfig.CLIENT.hud.hydrationBarCapacity);
			bar.setSpacing(ModConfig.CLIENT.hud.hydrationBarSpacing);
			hud.addElement(bar, new ElementPositioner(origin_x, origin_y, ModConfig.CLIENT.hud.hydrationBarX, ModConfig.CLIENT.hud.hydrationBarY));
			
			if(ModConfig.CLIENT.hud.moveAirBar) hud.addFilter(moveup, ElementType.AIR);
		}
		if(ModConfig.SANITY.enabled)
		{
			StatFillBar<SanityRecord> bar = new StatFillBar<SanityRecord>(SanityModifier.instance, Direction2D.UP, new TexturedElement(sanityicon, 0, 0, 16, 16, true));
			bar.addLayer(new TexturedElement(sanityicon, 16, 0, 16, 16, true), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(1);
			hud.addElement(bar, new ElementPositioner(origin_x, origin_y, ModConfig.CLIENT.hud.sanityIconX, ModConfig.CLIENT.hud.sanityIconY));
			hud.addFilter(moveup, ElementType.SUBTITLES);
		}
		if(ModConfig.GHOST.enabled)
		{
			Predicate<StatTracker> isGhostActive = (StatTracker tracker) -> tracker.getRecord(GhostProvider.instance).isActive();
			hud.addElement(new GhostEnergyBar(), new ElementPositioner(0.5F, 1F, -91, -39));
			hud.addFilterToAll(new HideRenderFilter<StatTracker>(isGhostActive), ElementType.HEALTH, ElementType.AIR, ElementType.ARMOR, ElementType.FOOD);
		}
		
		if((ModConfig.HEAT.enabled && ModConfig.CLIENT.hud.moveArmorBar) || (ModConfig.HYDRATION.enabled && ModConfig.CLIENT.hud.moveAirBar))
			hud.addFilterToAll(moveup, ElementType.CHAT);
	}
}