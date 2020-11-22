package enginecrafter77.survivalinc.client;

import java.util.function.Function;

public enum Axis2D {
	HORIZONTAL(OverlayElement::getWidth),
	VERTICAL(OverlayElement::getHeight);
	
	private final Function<OverlayElement<?>, Integer> dimensioner;
	
	private Axis2D(Function<OverlayElement<?>, Integer> dimensioner)
	{
		this.dimensioner = dimensioner;
	}
	
	public int getAxialDimension(OverlayElement<?> element)
	{
		return this.dimensioner.apply(element);
	}
}
