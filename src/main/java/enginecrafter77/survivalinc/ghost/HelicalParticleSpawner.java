package enginecrafter77.survivalinc.ghost;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;

public class HelicalParticleSpawner {
	public static final int CIRCLE_SUBDIVISIONS = 32;
	
	public final EnumParticleTypes type;
	
	private int helices;
	private double constantoffset;
	
	public HelicalParticleSpawner(EnumParticleTypes type)
	{
		this.type = type;
		this.setHelixCount(4);
	}
	
	public HelicalParticleSpawner setHelixCount(int helices)
	{
		this.constantoffset = (2D * Math.PI) / helices;
		this.helices = helices;
		return this;
	}
	
	public int getHelixCount()
	{
		return this.helices;
	}
	
	public double getHelixOffset(int helix)
	{
		return helix * this.constantoffset;
	}
	
	public void spawn(WorldClient world, Vec3d origin, Vec3d sizes, Vec3d motion, int tick)
	{
		double height = sizes.y * ((((1 + tick % HelicalParticleSpawner.CIRCLE_SUBDIVISIONS) / (double)HelicalParticleSpawner.CIRCLE_SUBDIVISIONS)) - 0.5D);
		
		for(int index = 0; index < this.getHelixCount(); index++)
		{
			double argument = (double)tick / (2D * Math.PI) + this.getHelixOffset(index);
			Vec3d position = origin.add(sizes.x * Math.sin(argument), height, sizes.z * Math.cos(argument));
			world.spawnParticle(this.type, position.x, position.y, position.z, motion.x, motion.y, motion.z);
		}
	}	
}
