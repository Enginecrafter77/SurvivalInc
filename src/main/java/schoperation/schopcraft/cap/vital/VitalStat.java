package schoperation.schopcraft.cap.vital;

import net.minecraft.entity.player.EntityPlayer;

public interface VitalStat {
	public void modifyStat(VitalStatType stat, float amount);
	public void setStat(VitalStatType stat, float amount);
	public float getStat(VitalStatType stat);
	
	public void updateStats(EntityPlayer player);
	public void punish(EntityPlayer player);
}