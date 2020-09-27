package enginecrafter77.survivalinc.stats.effect;

import enginecrafter77.survivalinc.stats.StatRecord;
import net.minecraft.entity.player.EntityPlayer;

@FunctionalInterface
public interface StatEffect<RECORD extends StatRecord> {
	public void apply(RECORD record, EntityPlayer player);
}
