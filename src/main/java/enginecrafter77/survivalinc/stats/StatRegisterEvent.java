package enginecrafter77.survivalinc.stats;

import java.util.Collection;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * StatRegisterEvent is an event designed to register
 * {@link StatProvider}s to a freshly-created {@link StatTracker}
 * instance.
 * @author Enginecrafter77
 */
public class StatRegisterEvent extends Event {
	/** The {@link StatTracker} instance being created */
	protected final StatTracker target;
	
	/** Creates a new StatRegisterEvent with the specified target {@link StatTracker tracker} */
	public StatRegisterEvent(StatTracker target)
	{
		this.target = target;
	}
	
	@Override
	public boolean isCancelable()
	{
		return true;
	}
	
	/**
	 * Registers the specified StatProvider in the underlying
	 * {@link SimpleStatRegister} instance.
	 * @param provider
	 */
	public void register(StatProvider<?> provider)
	{
		this.target.registerProvider(provider);
	}
	
	/**
	 * @return A set of already registered stat providers.
	 */
	public Collection<StatProvider<?>> getRegisteredProviders()
	{
		return this.target.getRegisteredProviders();
	}
}