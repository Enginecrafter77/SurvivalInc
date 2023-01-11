package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BlockPropertyView<TYPE> {
	public Optional<TYPE> getValueFor(Block block);

	public Stream<Map.Entry<Block, TYPE>> entryStream();

	public default Set<Map.Entry<Block, TYPE>> entrySet()
	{
		return this.entryStream().collect(Collectors.toSet());
	}
}
