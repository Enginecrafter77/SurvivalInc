package enginecrafter77.survivalinc.stats;

import java.util.concurrent.Callable;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraftforge.common.MinecraftForge;

/**
 * StatRegisterDispatcher is a factory for {@link StatTracker}s.
 * This class was introduced to grant additional freedom to
 * users willing to extends the functionality of Survival Inc.
 * stat trackers. The StatRegisterDispatcher is designed to be
 * fed to {@link CapabilityManager#register} as the factory callback.
 * Internally, the StatRegisterDispatcher constructs a {@link StatRegisterEvent},
 * which is then broadcasted on the {@link MinecraftForge#EVENT_BUS}.
 * The StatRegisterEvent takes care of registering the {@link StatProvider}s
 * to the stat tracker. When it's done, the stat tracker instance is
 * returned so the capability may be constructed.
 * @author Enginecrafter77
 */
public class StatRegisterDispatcher implements Callable<StatTracker> {
	
	public static final StatRegisterDispatcher instance = new StatRegisterDispatcher();
	
	protected Callable<StatTracker> factory;
	
	public StatRegisterDispatcher()
	{
		this.factory = SimpleStatRegister::new;
	}
	
	public void setTrackerFactory(Callable<StatTracker> factory)
	{
		this.factory = factory;
	}
	
	@Override
	public StatTracker call() throws Exception
	{
		StatRegisterEvent event = new StatRegisterEvent(factory.call());
		MinecraftForge.EVENT_BUS.post(event);
		SurvivalInc.logger.info("StatRegisterEvent resulted in {}", event.target.toString());
		return event.target;
	}
}
