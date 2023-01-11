package enginecrafter77.survivalinc.season.calendar;

import com.google.common.collect.ImmutableSet;
import enginecrafter77.survivalinc.season.AbstractSeason;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CalendarHashingCollector implements Collector<AbstractSeason, List<Integer>, Integer> {
	public static final CalendarHashingCollector INSTANCE = new CalendarHashingCollector();

	private static final int HASH_MAGIC_CONSTANT = 42; // In honor of Ing. A. Smrcka, PhD.

	public int seasonHash(AbstractSeason season)
	{
		return HASH_MAGIC_CONSTANT * season.getId().hashCode() + season.getLength();
	}

	@Override
	public Supplier<List<Integer>> supplier()
	{
		return ArrayList::new;
	}

	@Override
	public BiConsumer<List<Integer>, AbstractSeason> accumulator()
	{
		return (List<Integer> list, AbstractSeason season) -> list.add(this.seasonHash(season));
	}

	@Override
	public BinaryOperator<List<Integer>> combiner()
	{
		return (List<Integer> first, List<Integer> second) -> {
			first.addAll(second);
			return first;
		};
	}

	@Override
	public Function<List<Integer>, Integer> finisher()
	{
		return (List<Integer> hashCodes) -> {
			int sum = 0;
			for(Integer code : hashCodes)
				sum += code;
			return sum;
		};
	}

	@Override
	public Set<Characteristics> characteristics()
	{
		return ImmutableSet.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
	}
}
