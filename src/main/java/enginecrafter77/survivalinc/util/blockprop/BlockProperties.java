package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class BlockProperties implements BlockPropertyView<BlockPropertyMap> {
	protected abstract Map<Block, ? extends BlockPropertyMap> getBlockProperties();

	@Override
	public Optional<BlockPropertyMap> getValueFor(Block block)
	{
		return Optional.ofNullable(this.getBlockProperties().get(block));
	}

	@Override
	public Stream<Map.Entry<Block, BlockPropertyMap>> entryStream()
	{
		return this.entrySet().stream();
	}

	public <T> BlockPropertiesPrimitiveView<T> view(String key, Function<BlockPrimitiveProperty<?>, BlockPrimitiveProperty<T>> transformer)
	{
		return new BlockPropertiesPrimitiveView<T>(this, key, transformer);
	}

	public <T> BlockPropertiesPrimitiveView<T> view(String key, Class<T> type)
	{
		return this.view(key, BlockPrimitiveProperty.cast(type));
	}

	public <T> BlockPropertiesPrimitiveView<T> viewSingular(Class<T> type)
	{
		return this.view(BlockPropertyMap.KEY_SINGULAR_PROPERTY, type);
	}

	public <T> BlockPropertiesPrimitiveView<T> viewSingular(Function<BlockPrimitiveProperty<?>, BlockPrimitiveProperty<T>> transformer)
	{
		return this.view(BlockPropertyMap.KEY_SINGULAR_PROPERTY, transformer);
	}
}
