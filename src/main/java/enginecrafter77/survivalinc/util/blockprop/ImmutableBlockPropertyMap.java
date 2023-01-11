package enginecrafter77.survivalinc.util.blockprop;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;

import java.util.Map;

public class ImmutableBlockPropertyMap extends BlockPropertyMap {
	private final ImmutableMap<String, BlockPrimitiveProperty<?>> properties;

	public ImmutableBlockPropertyMap(Block block, Map<String, BlockPrimitiveProperty<?>> props)
	{
		super(block);
		this.properties = ImmutableMap.copyOf(props);
	}

	@Override
	public Map<String, BlockPrimitiveProperty<?>> getValue()
	{
		return this.properties;
	}

	public static ImmutableBlockPropertyMap from(Block block, Map<String, Object> props)
	{
		ImmutableMap.Builder<String, BlockPrimitiveProperty<?>> builder = ImmutableMap.builder();
		for(Map.Entry<String, Object> entry : props.entrySet())
			builder.put(entry.getKey(), new BlockPrimitiveProperty<Object>(block, entry.getValue()));
		return new ImmutableBlockPropertyMap(block, builder.build());
	}

	public static ImmutableBlockPropertyMap copyOf(BlockPropertyMap map)
	{
		if(map instanceof ImmutableBlockPropertyMap)
			return (ImmutableBlockPropertyMap)map;
		return new ImmutableBlockPropertyMap(map.getBlock(), ImmutableMap.copyOf(map.getValue()));
	}
}
