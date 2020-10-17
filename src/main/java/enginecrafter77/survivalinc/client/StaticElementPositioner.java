package enginecrafter77.survivalinc.client;

import java.util.Comparator;

import net.minecraft.client.gui.ScaledResolution;

public class StaticElementPositioner extends ElementPositioner {
	
	public static final Comparator<ScaledResolution> resolutionComparator = new Comparator<ScaledResolution>() {
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
	};
	
	private ScaledResolution computed;
	private int posX;
	private int posY;
	
	public StaticElementPositioner()
	{
		this.computed = null;
		this.posX = 0;
		this.posY = 0;
	}
	
	public boolean hasResolutionChanged(ScaledResolution resolution)
	{
		return StaticElementPositioner.resolutionComparator.compare(this.computed, resolution) != 0;
	}
	
	public void recomputePositionsFor(ScaledResolution resolution)
	{
		this.posX = super.getX(resolution);
		this.posY = super.getY(resolution);
		this.computed = resolution;
	}
	
	@Override
	public int getX(ScaledResolution resolution)
	{
		if(this.hasResolutionChanged(resolution))
			this.recomputePositionsFor(resolution);
		return this.posX;
	}
	
	@Override
	public int getY(ScaledResolution resolution)
	{
		if(this.hasResolutionChanged(resolution))
			this.recomputePositionsFor(resolution);
		return this.posY;
	}
	
}
