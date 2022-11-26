package enginecrafter77.survivalinc.util;

import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public class VectorOriginDistanceComparator implements Comparator<Vec3d> {
	private final Vec3d origin;

	public VectorOriginDistanceComparator(Vec3d source)
	{
		this.origin = source;
	}

	@Override
	public int compare(Vec3d o1, Vec3d o2)
	{
		double dist1 = o1.distanceTo(this.origin);
		double dist2 = o2.distanceTo(this.origin);
		return Double.compare(dist1, dist2);
	}
}
