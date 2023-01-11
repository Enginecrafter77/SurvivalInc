package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class BlockPropertiesPrimitiveView<VAL> implements BlockPropertyView<VAL> {
	private final BlockPropertyView<? extends BlockPropertyMap> parent;
	private final String key;
	private final Function<BlockPrimitiveProperty<?>, BlockPrimitiveProperty<VAL>> transformer;

	public BlockPropertiesPrimitiveView(BlockPropertyView<? extends BlockPropertyMap> parent, String key, Function<BlockPrimitiveProperty<?>, BlockPrimitiveProperty<VAL>> transformer)
	{
		this.parent = parent;
		this.transformer = transformer;
		this.key = key;
	}

	protected Optional<BlockPrimitiveProperty<VAL>> mapPropsOptional(BlockPropertyMap props)
	{
		return props.select(this.key).map(this.transformer);
	}

	@Nullable
	protected BlockPrimitiveProperty<VAL> mapPropsNullable(BlockPropertyMap props)
	{
		return this.mapPropsOptional(props).orElse(null);
	}

	@Override
	public Optional<VAL> getValueFor(Block block)
	{
		return parent.getValueFor(block).flatMap(this::mapPropsOptional).map(BlockPrimitiveProperty::getValue);
	}

	@Override
	public Stream<Map.Entry<Block, VAL>> entryStream()
	{
		return parent.entryStream().map(Map.Entry::getValue).map(this::mapPropsNullable).filter(Objects::nonNull).map(BlockPrimitiveProperty::asMapEntry);
	}
}
