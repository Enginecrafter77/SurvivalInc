package schoperation.schopcraft.cap.stat;

import java.io.Serializable;

import net.minecraft.entity.player.EntityPlayer;

public interface StatProvider extends Serializable {
	public float calculateChangeFor(EntityPlayer target);
	public String getStatID();
	
	public float getMaximum();
	public float getMinimum();
}
