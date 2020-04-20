package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;

public class SimpleStatContainer extends AbstractStatContainer {
	private static final long serialVersionUID = -5457336481767685017L;
	
	@Override
	public void recalculatePositions(ScaledResolution resolution)
	{
		this.setPosition(Axis.HORIZONTAL, resolution.getScaledWidth() / 2 + 95);
		this.setPosition(Axis.VERTICAL, resolution.getScaledHeight() - 2);
		
		int current = this.position.get(Axis.HORIZONTAL);
		for(StatBar bar : this)
		{
			// Set the positions to default
			bar.setPosition(Axis.VERTICAL, this.position.get(Axis.VERTICAL) - bar.getDimension(Axis.VERTICAL));
			// Set the spanning position
			bar.setPosition(Axis.HORIZONTAL, current);
			current += bar.getDimension(Axis.HORIZONTAL) + 2; // +2 => spacing
		}
	}

}
