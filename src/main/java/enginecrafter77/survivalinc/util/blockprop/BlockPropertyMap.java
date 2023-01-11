package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class BlockPropertyMap implements BlockPropertyHolder<Map<String, BlockPrimitiveProperty<?>>> {
	public static final String KEY_SINGULAR_PROPERTY = "<value>";

	public final Block block;

	protected BlockPropertyMap(Block block)
	{
		this.block = block;
	}

	public boolean isSingular()
	{
		return this.getValue().containsKey(KEY_SINGULAR_PROPERTY);
	}

	@Override
	public Block getBlock()
	{
		return this.block;
	}

	public Optional<BlockPrimitiveProperty<?>> select(String key)
	{
		return Optional.ofNullable(this.getValue().get(key));
	}

	public Optional<BlockPrimitiveProperty<?>> singular()
	{
		return this.select(KEY_SINGULAR_PROPERTY);
	}

	public static Function<BlockPropertyMap, Optional<BlockPrimitiveProperty<?>>> selecting(String key)
	{
		return (BlockPropertyMap map) -> map.select(key);
	}
}
