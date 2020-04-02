package schoperation.schopcraft.cap.vital;

import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;

//TODO DISTANT make map keys a common interface (VitalStatType would implement it, providing some basic values)
public class VitalStatCarrier extends HashMap<VitalStatType, Float> implements VitalStat {
	private static final long serialVersionUID = 7384655024432036857L;
	
	public VitalStatCarrier()
	{
		for(VitalStatType type : VitalStatType.values())
			this.setStat(type, type.max);
	}
	
	@Override
	public void modifyStat(VitalStatType stat, float amount)
	{
		float value = this.getStat(stat) + amount;
		if(value > stat.max) value = stat.max;
		if(value < stat.min) value = stat.min;
		this.setStat(stat, value);
	}

	@Override
	public void setStat(VitalStatType stat, float amount)
	{
		this.put(stat, amount);
	}

	@Override
	public float getStat(VitalStatType stat)
	{
		return this.get(stat);
	}
	
	@Override
	public void updateStats(EntityPlayer player)
	{
		for(VitalStatType stat : VitalStatType.values())
			this.modifyStat(stat, stat.calculateChange(player));
	}

	@Override
	public void punish(EntityPlayer player)
	{
		// blah blah blah
	}
}
