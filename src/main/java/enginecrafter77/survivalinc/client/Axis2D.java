package enginecrafter77.survivalinc.client;

import java.util.function.Function;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Axis2D is just what it's name suggests.
 * It's an enum specifying one of the 2 axes
 * on 2D plane: Horizontal and Vertical.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public enum Axis2D {
	HORIZONTAL(ScaledResolution::getScaledWidth),
	VERTICAL(ScaledResolution::getScaledHeight);
	
	private final Function<ScaledResolution, Integer> dimensionExtractor;
	
	private Axis2D(Function<ScaledResolution, Integer> dimensionExtractor)
	{
		this.dimensionExtractor = dimensionExtractor;
	}
	
	public int getResolutionDimension(ScaledResolution resolution)
	{
		return this.dimensionExtractor.apply(resolution);
	}
}
