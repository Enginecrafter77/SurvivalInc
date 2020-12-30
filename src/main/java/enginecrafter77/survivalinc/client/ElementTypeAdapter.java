package enginecrafter77.survivalinc.client;

import java.util.function.Function;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * ElementTypeAdapter serves as a simple transformer of the argument.
 * It is basically a delegate class that uses a {@link Function} to
 * transform it's input to the input accepted by it's target.
 * @author Enginecrafter77
 * @param <INPUT> The argument type accepted by this adapter
 * @param <FORWARD> The argument type of the target
 */
@SideOnly(Side.CLIENT)
public class ElementTypeAdapter<INPUT, FORWARD> implements OverlayElement<INPUT> {
	public final Function<INPUT, FORWARD> transformer;
	public final OverlayElement<FORWARD> target;
	
	public ElementTypeAdapter(OverlayElement<FORWARD> target, Function<INPUT, FORWARD> transformer)
	{
		this.transformer = transformer;
		this.target = target;
	}
	
	protected ElementTypeAdapter(OverlayElement<FORWARD> target)
	{
		this.transformer = null;
		this.target = target;
	}
	
	public FORWARD transformArgument(INPUT input)
	{
		return this.transformer.apply(input);
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, INPUT arg)
	{
		this.target.draw(resolution, position, partialTicks, this.transformArgument(arg));
	}

	@Override
	public int getSize(Axis2D axis)
	{
		return target.getSize(axis);
	}

}
