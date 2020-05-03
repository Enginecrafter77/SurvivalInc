package enginecrafter77.survivalinc.client;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHUD extends AbstractStatContainer {
	private static final long serialVersionUID = -3636268515627373812L;

	public static final RenderHUD instance = new RenderHUD();
	
	public ScriptEngine engine;
	public Bindings values;
	
	public RenderHUD()
	{
		ScriptEngineManager manager = new ScriptEngineManager(ClassLoader.getSystemClassLoader());
		this.engine = manager.getEngineByName("nashorn");
		assert this.engine != null : "JavaScript engine not found!";
		
		this.values = engine.createBindings();
	}
	
	@Override
	public void recalculatePositions(ScaledResolution resolution)
	{
		values.put("$w", resolution.getScaledWidth());
		values.put("$h", resolution.getScaledHeight());
		
		for(Axis axis : Axis.values())
		{
			String expression = ModConfig.CLIENT.statBarPosition[axis.ordinal()];
			try
			{
				Object result = engine.eval(expression, values);
				this.setPosition(axis, (int)Math.round((Double)result));
			}
			catch(ScriptException exc)
			{
				System.err.format("Caught exception while processing expression \"%s\": ", expression);
				exc.printStackTrace();
			}
		}
		
		int current = this.position.get(Axis.HORIZONTAL);
		for(StatBar bar : this)
		{
			// Set the positions to default
			bar.setPosition(Axis.VERTICAL, this.position.get(Axis.VERTICAL) - bar.getDimension(Axis.VERTICAL));
			// Set the spanning position
			bar.setPosition(Axis.HORIZONTAL, current);
			current += bar.getDimension(Axis.HORIZONTAL) + 2; // +2 => spacing
		}
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if(event.getType() != ElementType.HOTBAR) return;
		
		StatTracker tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		this.draw(event.getResolution(), tracker);
	}
}