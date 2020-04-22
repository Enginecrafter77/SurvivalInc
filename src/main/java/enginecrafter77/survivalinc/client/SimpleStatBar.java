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
public class SimpleStatBar extends Gui implements StatBar {
	public final StatProvider key;
	
	protected final DifferentialArrow arrow;
	protected final GaugeBar gauge;
	
	protected final ResourceLocation texture;
	protected int texoffx, texoffy, texwidth, texheight;
	protected int iconheight, spacing;
	
	public EnumMap<Axis, Integer> position;
	
	public float previousValue;
	
	public SimpleStatBar(StatProvider key, ResourceLocation icon, Color color)
	{
		this(key, icon, 0, 0, color);
	}
	
	public SimpleStatBar(StatProvider key, ResourceLocation texture, int texture_x, int texture_y, Color color)
	{
		this.previousValue = 0;
		
		this.arrow = new DifferentialArrow(key, 8, 12);		
		this.gauge = new GaugeBar(8, 32, color);
		this.texture = texture;
		this.texoffx = texture_x;
		this.texoffy = texture_y;
		this.key = key;
		
		this.texheight = 12;
		this.texwidth = 8;
		this.iconheight = 12;
		this.spacing = 2;
		
		this.position = new EnumMap<Axis, Integer>(Axis.class);
	}
	
	@Override
	public void draw(ScaledResolution resolution, StatTracker stats) throws UnsupportedOperationException
	{
		try
		{
			this.gauge.draw(this.position.get(Axis.HORIZONTAL), this.position.get(Axis.VERTICAL), this.getStatProportional(stats));
		}
		catch(NullPointerException exc)
		{
			UnsupportedOperationException nexc = new UnsupportedOperationException("Server doesn't track stat " + key.getStatID() + ". Some other mod on client's side is overriding default implementation.");
			nexc.initCause(exc);
			throw nexc;
		}
		
		GlStateManager.enableAlpha(); // Enable alpha, we will need it
		
		// Draw the stat icon
		this.gauge.texturer.bindTexture(texture);
		Gui.drawModalRectWithCustomSizedTexture(this.position.get(Axis.HORIZONTAL), this.position.get(Axis.VERTICAL) + this.gauge.height + spacing, texoffx, texoffy, this.gauge.width, iconheight, texwidth, texheight);
		
		// Draw the arrow indicating value change TODO add onGeometryChange method to recalculate the position
		this.arrow.setPosition(Axis.HORIZONTAL, this.position.get(Axis.HORIZONTAL));
		this.arrow.setPosition(Axis.VERTICAL, this.position.get(Axis.VERTICAL) - this.arrow.height - this.spacing);
		this.arrow.draw(resolution, stats);
	}
	
	@Override
	public void setPosition(Axis axis, int position)
	{
		this.position.put(axis, position);
	}
	
	public float getStatProportional(StatTracker tracker) throws NullPointerException
	{
		return (tracker.getStat(key) - key.getMinimum()) / (key.getMaximum() - key.getMinimum());
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
		switch(axis)
		{
		case HORIZONTAL:
			return this.gauge.width;
		case VERTICAL:
			return this.gauge.height + spacing + iconheight;
		default:
			return 0;
		}
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
			GlStateManager.disableAlpha(); // Disable alpha, just in case
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
			float scale = 10F * tracker.getRecord(this.provider).getLastChange();
			float dist = Math.abs(scale);
			if(dist > 1F) scale /= dist; // Always results in 1 or -1
			if(dist < 0.3F && dist != 0) scale = scale > 0F ? 0.3F : -0.3F;
			return scale;
		}
		
	}
}