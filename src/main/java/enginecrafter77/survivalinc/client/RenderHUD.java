package enginecrafter77.survivalinc.client;

import java.util.LinkedList;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHUD extends LinkedList<OverlayElement> {
	private static final long serialVersionUID = -5194965038466841973L;

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
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		for(OverlayElement element : this)
			element.draw(event);
	}
}