package enginecrafter77.survivalinc.stats.effect;

import java.util.LinkedList;
import java.util.List;

import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.entity.player.EntityPlayer;

public class FunctionalCalculator implements CalculatorFunction, StatEffect<SimpleStatRecord> {

	public final List<CalculatorFunction> functions;
	
	public FunctionalCalculator()
	{
		this.functions = new LinkedList<CalculatorFunction>();
	}
	
	public void add(CalculatorFunction function)
	{
		this.functions.add(function);
	}
	
	@Override
	public void apply(SimpleStatRecord record, EntityPlayer player)
	{
		float value = record.getValue();
		value = this.apply(player, value);
		record.setValue(value);
	}

	@Override
	public float apply(EntityPlayer player, float value)
	{
		for(CalculatorFunction function : this.functions)
		{
			value = function.apply(player, value);
		}
		return value;
	}

}
