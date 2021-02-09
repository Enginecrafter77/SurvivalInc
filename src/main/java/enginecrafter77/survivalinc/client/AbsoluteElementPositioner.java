package enginecrafter77.survivalinc.client;

import java.util.EnumMap;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * ElementPositioner is a class used to dynamically compute position
 * of an on-screen element, using simple mathematical formula. Each
 * ElementPositioner instance can be described by four variables: {@link #mulX},
 * {@link #mulY}, {@link #offX}, {@link #offY}. Each of these variables are used
 * to compute the resultant X and Y positions in {@link #calculateX(ScaledResolution)}
 * and {@link #calculateY(ScaledResolution)} respectively.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public class AbsoluteElementPositioner implements ElementPositioner {
	
	protected final ImmutableMap<Axis2D, Integer> offset;
	protected final ImmutableMap<Axis2D, Float> origin;
	
	public AbsoluteElementPositioner(float mx, float my, int ox, int oy)
	{
		this.offset = ImmutableMap.of(Axis2D.HORIZONTAL, ox, Axis2D.VERTICAL, oy);
		this.origin = ImmutableMap.of(Axis2D.HORIZONTAL, mx, Axis2D.VERTICAL, my);
	}
	
	public Position2D getPositionFor(ScaledResolution resolution, OverlayElement<?> element)
	{
		EnumMap<Axis2D, Integer> position = new EnumMap<Axis2D, Integer>(Axis2D.class);
		
		for(Axis2D axis : Axis2D.values())
		{
			int axialpos = (int)((float)axis.getResolutionDimension(resolution) * this.origin.get(axis)) + this.offset.get(axis);
			position.put(axis, axialpos);
		}
		
		return new Position2D(position);
	}
	
}
