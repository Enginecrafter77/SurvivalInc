package enginecrafter77.survivalinc.util;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The modifier calculator class is basically a compatibility
 * layer for ModifierApplicator and former implementation of
 * ModifierCalculator. In future releases, it will be removed.
 * @author Enginecrafter77
 * @param <TARGET>
 */
public class ModifierCalculator<TARGET> extends ModifierApplicator<TARGET> {
	private static final long serialVersionUID = 8665966731867281957L;
	
	public void addModifier(Function<TARGET, Float> function, OperationType operation)
	{
		this.put(new FunctionalModifier<TARGET>(function), operation);
	}
	
	public void addConditionalModifier(Predicate<TARGET> check, Float value, OperationType operation)
	{
		this.put(new ConditionalModifier<TARGET>(check, value), operation);
	}
	
	public void addConstantModifier(Float value, OperationType operation)
	{
		this.put(new ConditionalModifier<TARGET>((TARGET target) -> true, value), operation);
	}
}