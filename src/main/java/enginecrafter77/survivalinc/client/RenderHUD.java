package enginecrafter77.survivalinc.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHUD extends Gui implements IMessageHandler<StatUpdateMessage, IMessage> {
	
	public static final RenderHUD instance = new RenderHUD();
	
	public final List<StatBar> statbars;
	private StatTracker tracker;
	
	private RenderHUD()
	{
		this.tracker = StatRegister.CAPABILITY.getDefaultInstance();
		this.statbars = new LinkedList<StatBar>();
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if(event.getType() != ElementType.HOTBAR) return;
		ScaledResolution resolution = event.getResolution();
		
		int x = resolution.getScaledWidth() / 2 + 95;
		
		for(StatBar bar : this.statbars)
		{
			try
			{
				bar.draw(tracker, x, resolution.getScaledHeight() - bar.spacing - bar.getTotalHeight());
				x += bar.width + bar.spacing;
			}
			catch(NullPointerException exc)
			{
				System.err.format("Server doesn't track stat %s! Some other mod on client's side is overriding default implementation." + bar.key.getStatID());
			}
		}
	}
	
	@Override
	public IMessage onMessage(StatUpdateMessage message, MessageContext ctx)
	{
		for(Entry<StatProvider, Float> entry : message.tracker)
			this.tracker.setStat(entry.getKey(), message.tracker.getStat(entry.getKey()));
		return null;
	}
}