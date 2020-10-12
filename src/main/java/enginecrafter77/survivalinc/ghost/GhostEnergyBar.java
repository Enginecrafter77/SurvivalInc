package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.ScalableOverlayElement;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GhostEnergyBar extends ScalableOverlayElement {
	
	public static final ResourceLocation texture = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/ghostenergy.png");
	
	public static final int count = 10;
	
	private StatTracker tracker;
	
	public GhostEnergyBar()
	{
		super(GhostEnergyBar.count * 9, 9);
		this.setAbsolutePosition(-91, -39);
		this.setRelativePositionBase(0.5F, 1F);
	}
	
	@Override
	public void draw(RenderGameOverlayEvent event)
	{
		if(this.getRecord().isActive())
		{
			switch(event.getType())
			{
			case FOOD:
				super.draw(event);
			case ARMOR:
			case HEALTH:
				event.setCanceled(true);
			default:
				break;
			}
		}
	}
	
	@Override
	public void draw()
	{
		Minecraft instance = Minecraft.getMinecraft();
		GhostEnergyRecord energy = this.getRecord();
		float value = GhostEnergyBar.count * energy.getValue() / energy.valuerange.upperEndpoint();
		
		instance.getTextureManager().bindTexture(texture);
		GlStateManager.enableAlpha();
		
		for(int current = 0; current < GhostEnergyBar.count; current++)
		{
			int offset = this.getX() + current * 8;
			Gui.drawModalRectWithCustomSizedTexture(offset, this.getY(), 0, 0, 9, this.getHeight(), 9, 18);
			
			if(value > 0)
			{
				float part = value > 1F ? 1F : value;
				Gui.drawModalRectWithCustomSizedTexture(offset, this.getY(), 0, 9, Math.round(part * 9F), this.getHeight(), 9, 18);
				value -= part;
			}
		}
		GlStateManager.disableAlpha();
	}
	
	public GhostEnergyRecord getRecord()
	{
		if(this.tracker == null) this.tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		return (GhostEnergyRecord)this.tracker.getRecord(SurvivalInc.proxy.ghost);
	}

}
