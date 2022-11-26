package enginecrafter77.survivalinc.stats.effect;

import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.entity.player.EntityPlayer;

import java.util.LinkedList;
import java.util.List;

/**
 * FunctionalCalculator is a simple mechanism for evaluating
 * multiple {@link CalculatorFunction}. The resultant value
 * from one function is passed onto another, thus forming an
 * evaluation chain. The return value from the last function
 * is returned by the calculator. The calculator can also serve
 * as a {@link StatEffect} on {@link SimpleStatRecord}. This
 * way the initial value is taken from the record, and is stored
 * in it afterwards.
 * @author Enginecrafter77
 */
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
