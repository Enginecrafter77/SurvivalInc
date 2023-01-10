package enginecrafter77.survivalinc.stats;

public class UnknownStatException extends RuntimeException {
	private final StatProvider<?> provider;

	public UnknownStatException(StatProvider<?> provider, String message)
	{
		super(message);
		this.provider = provider;
	}

	public UnknownStatException(StatProvider<?> provider)
	{
		this(provider, "Passing unregistered provider " + provider.getStatID() + " to a stat tracker.");
	}

	public StatProvider<?> getOffendingProvider()
	{
		return this.provider;
	}
}
