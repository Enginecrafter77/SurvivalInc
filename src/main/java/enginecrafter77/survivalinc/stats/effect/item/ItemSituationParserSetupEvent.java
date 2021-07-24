package enginecrafter77.survivalinc.stats.effect.item;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ItemSituationParserSetupEvent extends Event {

	public final ItemSituationParser parser;
	
	public ItemSituationParserSetupEvent(ItemSituationParser parser)
	{
		this.parser = parser;
	}
	
	public ItemSituationParserSetupEvent()
	{
		this(new ItemSituationParser());
	}
	
	public ItemSituationParser getParser()
	{
		return this.parser;
	}
	
	public void registerSituation(String name, ItemSituationParser.SituationEffectFactory factory)
	{
		this.parser.addSituationFactory(name, factory);
	}
	
}
