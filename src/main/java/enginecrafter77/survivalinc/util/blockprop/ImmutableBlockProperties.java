package enginecrafter77.survivalinc.util.blockprop;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;

import java.util.Map;

public class ImmutableBlockProperties extends BlockProperties {
	private final ImmutableMap<Block, ImmutableBlockPropertyMap> mappings;

	protected ImmutableBlockProperties(Map<Block, ImmutableBlockPropertyMap> mappings)
	{
		this.mappings = ImmutableMap.copyOf(mappings);
	}

	@Override
	protected Map<Block, ? extends BlockPropertyMap> getBlockProperties()
	{
		return this.mappings;
	}

	public static ImmutableBlockPropertiesBuilder builder()
	{
		return new ImmutableBlockPropertiesBuilder();
	}

	public static ImmutableBlockProperties from(Map<Block, ? extends BlockPropertyMap> blockPropertiesMap)
	{
		ImmutableMap.Builder<Block, ImmutableBlockPropertyMap> builder = ImmutableMap.builder();
		for(Map.Entry<Block, ? extends BlockPropertyMap> entry : blockPropertiesMap.entrySet())
			builder.put(entry.getKey(), ImmutableBlockPropertyMap.copyOf(entry.getValue()));
		return new ImmutableBlockProperties(builder.build());
	}

	public static ImmutableBlockProperties copyOf(BlockProperties source)
	{
		if(source instanceof ImmutableBlockProperties)
			return (ImmutableBlockProperties)source;
		return from(source.getBlockProperties());
	}
}
