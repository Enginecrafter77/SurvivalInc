package enginecrafter77.survivalinc.stats.modifier.ng;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public abstract class LocalPlayerEffect implements StatEffect {

	public abstract void applyToLocalPlayer(EntityPlayerSP player, float current);
	
	@Override
	public final float apply(EntityPlayer player, float current)
	{
		Minecraft client = Minecraft.getMinecraft();
		if(player == client.player)
			this.applyToLocalPlayer(client.player, current);
		return current;
	}
	
	@Override
	public final Side sideOnly()
	{
		return Side.CLIENT;
	}

}
