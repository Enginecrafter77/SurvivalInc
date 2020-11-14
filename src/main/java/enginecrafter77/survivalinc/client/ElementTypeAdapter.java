package enginecrafter77.survivalinc.client;

import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

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
	public Set<ElementType> disableElements(INPUT arg)
	{
		return target.disableElements(this.transformArgument(arg));
	}

	@Override
	public int getWidth()
	{
		return target.getWidth();
	}

	@Override
	public int getHeight()
	{
		return target.getHeight();
	}

}
