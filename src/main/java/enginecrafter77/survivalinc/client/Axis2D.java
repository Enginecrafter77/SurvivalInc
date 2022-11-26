package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;

import java.util.function.Function;

/**
 * Axis2D is just what it's name suggests. It's an enum specifying one of the 2 axes on 2D plane: Horizontal and Vertical.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public enum Axis2D {
	HORIZONTAL(ReadableDimension::getWidth, ReadablePoint::getX),
	VERTICAL(ReadableDimension::getHeight, ReadablePoint::getY);
	
	private final Function<ReadableDimension, Integer> dimensionExtractor;
	private final Function<ReadablePoint, Integer> positionExtractor;
	
	private Axis2D(Function<ReadableDimension, Integer> dimensionExtractor, Function<ReadablePoint, Integer> positionExtractor)
	{
		this.dimensionExtractor = dimensionExtractor;
		this.positionExtractor = positionExtractor;
	}
	
	public int getResolutionAxialValue(ScaledResolution resolution)
	{
		return this.getDimensionAxialValue(Axis2D.getResolutionDimensions(resolution));
	}
	
	public int getDimensionAxialValue(ReadableDimension dimension)
	{
		return this.dimensionExtractor.apply(dimension);
	}
	
	public int getPointAxialValue(ReadablePoint point)
	{
		return this.positionExtractor.apply(point);
	}
	
	public static ReadableDimension getResolutionDimensions(ScaledResolution resolution)
	{
		return new Dimension(resolution.getScaledWidth(), resolution.getScaledHeight());
	}
}
