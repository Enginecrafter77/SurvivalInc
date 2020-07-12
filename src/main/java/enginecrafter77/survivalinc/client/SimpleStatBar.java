package enginecrafter77.survivalinc.client;

import java.awt.Color;
import java.util.EnumMap;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimpleStatBar extends GaugeBar implements StatBar {
	public final StatProvider key;
	
	protected final DifferentialArrow arrow;
	
	protected final ResourceLocation texture;
	protected int texoffx, texoffy, texwidth, texheight;
	protected int iconheight, spacing;
	
	public SimpleStatBar(StatProvider key, ResourceLocation icon, Color color)
	{
		this(key, icon, 0, 0, color);
	}
	
	public SimpleStatBar(StatProvider key, ResourceLocation texture, int texture_x, int texture_y, Color color)
	{
		super(8, 32, color);
		
		this.arrow = new DifferentialArrow(key, 8, 12);
		this.texture = texture;
		this.texoffx = texture_x;
		this.texoffy = texture_y;
		this.key = key;
		
		this.texheight = 12;
		this.texwidth = 8;
		this.iconheight = 12;
		this.spacing = 2;
	}
	
	@Override
	protected float getProportion(StatTracker tracker)
	{
		return (tracker.getStat(key) - key.getMinimum()) / (key.getMaximum() - key.getMinimum());
	}
	
	@Override
	public void draw(ScaledResolution resolution, StatTracker stats) throws UnsupportedOperationException
	{
		if(!this.key.isAcitve(Minecraft.getMinecraft().player)) return;
		
		super.draw(resolution, stats);
		
		GlStateManager.enableAlpha(); // Enable alpha, we will need it
		// Draw the stat icon
		this.texturer.bindTexture(texture);
		Gui.drawModalRectWithCustomSizedTexture(this.position.get(Axis.HORIZONTAL), this.position.get(Axis.VERTICAL) + this.height + spacing, texoffx, texoffy, this.width, iconheight, texwidth, texheight);
		// Draw the arrow indicating value
		this.arrow.draw(resolution, stats);
		GlStateManager.disableAlpha(); // Disable alpha, just in case
	}
	
	@Override
	public void setPosition(Axis axis, int position)
	{
		this.position.put(axis, position);
		
		if(axis == Axis.VERTICAL)
			position -= (this.arrow.height + this.spacing);
		
		this.arrow.setPosition(axis, position);
	}

	@Override
	public StatProvider getProvider()
	{
		return this.key;
	}

	@Override
	public Axis getMajorAxis()
	{
		return Axis.VERTICAL;
	}

	@Override
	public int getDimension(Axis axis)
	{
		int value = super.getDimension(axis);
		if(axis == Axis.VERTICAL)
			value += spacing + iconheight;
		return value;
	}
	
	public static class DifferentialArrow implements StatRender
	{
		public static final ResourceLocation arrowtexture = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/arrow.png");
		
		public final EnumMap<Axis, Integer> position;
		public final TextureManager texturer;
		public final StatProvider provider;
		
		public int width, height;
		
		public DifferentialArrow(StatProvider provider, int width, int height)
		{
			this.texturer = Minecraft.getMinecraft().getTextureManager();
			this.position = new EnumMap<Axis, Integer>(Axis.class);
			this.provider = provider;
			this.height = height;
			this.width = width;
		}
		
		@Override
		public void draw(ScaledResolution resolution, StatTracker tracker)
		{
			// Draw the arrow
			this.texturer.bindTexture(arrowtexture);
			float value = this.getArrowValue(tracker);
			boolean inverse = value < 0F;
			value = Math.abs(value);
			
			GlStateManager.pushMatrix(); // Create new object by pushing matrix
			// Offset this object into the desired position + centering offset
			GlStateManager.translate(this.position.get(Axis.HORIZONTAL) + (this.width / 2), this.position.get(Axis.VERTICAL) + (this.height / 2), 0F);
			GlStateManager.pushMatrix(); // Create new object by pushing matrix
			GlStateManager.scale(value, value, 1F); // Scale the arrow
			if(inverse) GlStateManager.rotate(180F, 0F, 0F, 1F); // Rotate the arrow
			Gui.drawModalRectWithCustomSizedTexture(-this.width / 2, -this.height / 2, 0, 0, this.width, this.height, 8, 12); // Draw the arrow (center at origin)
			GlStateManager.popMatrix(); // Render the scaled and rotated arrow
			GlStateManager.popMatrix(); // Render the offset arrow in place
		}

		@Override
		public int getDimension(Axis axis)
		{
			switch(axis)
			{
			case HORIZONTAL:
				return this.width;
			case VERTICAL:
				return this.height;
			default:
				return 0;
			}
		}

		@Override
		public void setPosition(Axis axis, int value)
		{
			this.position.put(axis, value);
		}
		
		public float getArrowValue(StatTracker tracker)
		{
			// TODO Reimplement change tracking method in StatTracker implementations
			float scale = 0F; //10F * tracker.getRecord(this.provider).getLastChange();
			float dist = Math.abs(scale);
			if(dist > 1F) scale /= dist; // Always results in 1 or -1
			if(dist < 0.3F && dist != 0) scale = scale > 0F ? 0.3F : -0.3F;
			return scale;
		}
		
	}
}