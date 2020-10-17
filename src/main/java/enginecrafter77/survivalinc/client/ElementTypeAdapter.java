package enginecrafter77.survivalinc.client;

import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class ElementTypeAdapter<TARGET, SOURCE> implements OverlayElement<TARGET> {
	public final Function<TARGET, SOURCE> transformer;
	public final OverlayElement<SOURCE> target;
	
	public ElementTypeAdapter(OverlayElement<SOURCE> target, Function<TARGET, SOURCE> transformer)
	{
		this.transformer = transformer;
		this.target = target;
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, TARGET arg)
	{
		this.target.draw(resolution, position, partialTicks, this.transformer.apply(arg));
	}

	@Override
	public Set<ElementType> disableElements(TARGET arg)
	{
		return target.disableElements(transformer.apply(arg));
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
