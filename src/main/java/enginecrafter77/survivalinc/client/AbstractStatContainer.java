package enginecrafter77.survivalinc.client;

import java.util.EnumMap;
import java.util.LinkedList;

import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.ScaledResolution;

public abstract class AbstractStatContainer extends LinkedList<StatBar> implements StatRender {
	private static final long serialVersionUID = 1388496309303573903L;

	public EnumMap<Axis, Integer> position;
	
	private ScaledResolution calculationsResolution;
	
	public AbstractStatContainer()
	{
		this.position = new EnumMap<Axis, Integer>(Axis.class);
		this.calculationsResolution = null; // Forces update
	}
	
	public abstract void recalculatePositions(ScaledResolution resolution);
	
	@Override
	public void draw(ScaledResolution resolution, StatTracker tracker)
	{
		if(!resolution.equals(this.calculationsResolution))
		{
			this.recalculatePositions(resolution);
			this.calculationsResolution = resolution;
		}
		
		for(StatBar bar : this) bar.draw(resolution, tracker);
	}

	@Override
	public int getDimension(Axis axis)
	{
		int counter = 0;
		for(StatBar bar : this)
			counter += bar.getDimension(axis);
		return counter;
	}

	@Override
	public void setPosition(Axis axis, int value)
	{
		this.position.put(axis, value);
	}

}
