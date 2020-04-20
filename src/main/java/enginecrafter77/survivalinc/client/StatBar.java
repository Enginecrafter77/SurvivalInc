package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.stats.StatProvider;

public interface StatBar extends StatRender {
	public StatProvider getProvider();
	public Axis getMajorAxis();
}
