package enginecrafter77.survivalinc.client;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.ScaledResolution;

public abstract class AbstractStatContainer extends LinkedList<StatBar> implements StatRender, Comparator<ScaledResolution> {
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
		if(this.compare(resolution, calculationsResolution) != 0)
		{
			SurvivalInc.logger.debug("Resolution change detected. Recalculating GUI position...");
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

	/**
	 * A simple algorithm to check whether the two resolutions differ.
	 * @param first The first resolution
	 * @param second The second resolution
	 * @return 0 if they are equal. -1 if the first is bigger than the second and 1 if the second is bigger than the first.
	 */
	@Override
	public int compare(ScaledResolution first, ScaledResolution second)
	{
		if(first == null && second == null) return 0; // If both are null, they are equal
		if(first == null) return 1; // If only first is null, the second is automatically bigger
		if(second == null) return -1; // If only second is null, the first is automatically bigger
		
		int fpixels = first.getScaledWidth() * first.getScaledHeight() * first.getScaleFactor();
		int spixels = second.getScaledWidth() * second.getScaledHeight() * first.getScaleFactor();
		
		if(fpixels == spixels)
		{
			float fratio = (float)(first.getScaledWidth_double() / first.getScaledHeight_double());
			float sratio = (float)(second.getScaledWidth_double() / second.getScaledHeight_double());
			return Float.compare(fratio, sratio);
		}
		
		return Integer.compare(fpixels, spixels);
	}

}
