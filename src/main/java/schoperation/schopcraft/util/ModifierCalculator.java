package schoperation.schopcraft.util;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ModifierCalculator<TARGET> extends HashMap<Function<TARGET, Float>, OperationType> implements BiFunction<TARGET, Float, Float> {
	private static final long serialVersionUID = 1140172840070066395L;

	public void addModifier(Function<TARGET, Float> function, OperationType operation)
	{
		this.put(function, operation);
	}
	
	public void addConditionalModifier(Predicate<TARGET> check, Float value, OperationType operation)
	{
		this.addModifier((TARGET target) -> check.test(target) ? value : 0F, operation);
	}
	
	public void addConstantModifier(Float value, OperationType operation)
	{
		this.addModifier((TARGET target) -> value, operation);
	}
	
	@Override
	public Float apply(TARGET target, Float current)
	{
		for(Entry<Function<TARGET, Float>, OperationType> mod : this.entrySet())
			current = mod.getValue().apply(current, mod.getKey().apply(target));
		return current;
	}
}