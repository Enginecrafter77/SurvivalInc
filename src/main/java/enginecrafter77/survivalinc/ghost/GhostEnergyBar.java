package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.StatBar;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GhostEnergyBar extends Gui implements StatBar {
	
	public static final ResourceLocation texture = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/ghostenergy.png");
	
	public int[] parameters;
	
	public int count;
	
	public GhostEnergyBar()
	{
		this.parameters = new int[4];
		
		this.count = 10;
		this.parameters[2] = this.count * 8;
		this.parameters[3] = 9;
	}
	
	@Override
	public void draw(ScaledResolution resolution, StatTracker tracker)
	{
		Minecraft instance = Minecraft.getMinecraft();
		Ghost ghost = instance.player.getCapability(GhostProvider.target, null);
		if(ghost.getStatus())
		{
			this.setPosition(Axis.HORIZONTAL, resolution.getScaledWidth() / 2 - 91);
			this.setPosition(Axis.VERTICAL, resolution.getScaledHeight() - 39);
			
			float value = this.count * tracker.getStat(this.getProvider()) / this.getProvider().getMaximum(), part;
			
			instance.getTextureManager().bindTexture(texture);
			GlStateManager.enableAlpha();
			
			for(int current = 0; current < this.count; current++)
			{
				int offset = this.parameters[0] + (current * 8);
				Gui.drawModalRectWithCustomSizedTexture(offset, this.parameters[1], 0, 0, 9, this.parameters[3], 9, 18);
				
				if(value > 0)
				{
					part = value > 1F ? 1F : value;
					Gui.drawModalRectWithCustomSizedTexture(offset, this.parameters[1], 0, 9, Math.round(part * 9F), this.parameters[3], 9, 18);
					value -= part;
				}
			}
			GlStateManager.disableAlpha();
		}
	}

	@Override
	public int getDimension(Axis axis)
	{
		return this.parameters[axis.ordinal() + 2];
	}

	@Override
	public void setPosition(Axis axis, int value)
	{
		this.parameters[axis.ordinal()] = value;
	}

	@Override
	public StatProvider getProvider()
	{
		return GhostEnergy.instance;
	}

	@Override
	public Axis getMajorAxis()
	{
		return Axis.HORIZONTAL;
	}

}
