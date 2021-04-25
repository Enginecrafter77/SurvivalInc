package enginecrafter77.survivalinc.client;

import java.util.function.Function;

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
	
	/**
	 * Transforms the local argument type into the
	 * argument type required by the delegated element.
	 * @param input The input argument
	 * @return The argument to feed the delegated element with
	 */
	public FORWARD transformArgument(INPUT input)
	{
		if(this.transformer == null)
			throw new IllegalArgumentException("Delegated element can't be null!");
		
		return this.transformer.apply(input);
	}
	
	@Override
	public void draw(Position2D position, float partialTicks, INPUT arg)
	{
		this.target.draw(position, partialTicks, this.transformArgument(arg));
	}

	@Override
	public int getSize(Axis2D axis)
	{
		return target.getSize(axis);
	}

}
