package enginecrafter77.survivalinc.client;

import java.util.function.Function;

import net.minecraft.client.gui.ScaledResolution;

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
