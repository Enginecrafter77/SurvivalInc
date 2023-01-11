package enginecrafter77.survivalinc.util.blockprop;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ImmutableBlockPropertiesBuilder implements BlockPropertiesBuilder<ImmutableBlockProperties> {
	private final Map<Block, ImmutableMap.Builder<String, Object>> builderMap;

	public ImmutableBlockPropertiesBuilder()
	{
		this.builderMap = new HashMap<Block, ImmutableMap.Builder<String, Object>>();
	}

	@Nonnull
	protected ImmutableMap.Builder<String, Object> getPropertiesFor(Block block)
	{
		return this.builderMap.computeIfAbsent(block, (Block key) -> ImmutableMap.builder());
	}

	@Override
	public void put(Block block, String key, Object value)
	{
		this.getPropertiesFor(block).put(key, value);
	}

	@Override
	public ImmutableBlockProperties build()
	{
		ImmutableMap.Builder<Block, ImmutableBlockPropertyMap> builder = ImmutableMap.builder();
		for(Map.Entry<Block, ImmutableMap.Builder<String, Object>> entry : this.builderMap.entrySet())
		{
			Block block = entry.getKey();
			Map<String, Object> props = entry.getValue().build();
			builder.put(block, ImmutableBlockPropertyMap.from(block, props));
		}
		return new ImmutableBlockProperties(builder.build());
	}
}
