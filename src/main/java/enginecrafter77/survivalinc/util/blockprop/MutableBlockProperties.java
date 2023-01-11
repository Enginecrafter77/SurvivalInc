package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

import java.util.HashMap;
import java.util.Map;

public class MutableBlockProperties extends BlockProperties {
	private final HashMap<Block, MutableBlockPropertyMap> map;

	public MutableBlockProperties()
	{
		this.map = new HashMap<Block, MutableBlockPropertyMap>();
	}

	public BlockPropertiesBuilder<MutableBlockProperties> editingBuilder()
	{
		return new MutableBlockPropertiesEditor();
	}

	public void importProperties(BlockProperties properties)
	{
		properties.entryStream().forEach(this::importPropertyMapEntry);
	}

	public void put(Block block, String key, Object value)
	{
		MutableBlockPropertyMap map = this.getValueFor(block).map(MutableBlockPropertyMap.class::cast).orElse(null);
		if(map == null)
		{
			map = new MutableBlockPropertyMap(block);
			this.map.put(block, map);
		}
		map.put(key, value);
	}

	public void putSingular(Block block, Object value)
	{
		this.put(block, BlockPropertyMap.KEY_SINGULAR_PROPERTY, value);
	}

	public void remove(Block block, String key)
	{
		this.getValueFor(block).map(MutableBlockPropertyMap.class::cast).ifPresent((MutableBlockPropertyMap map) -> map.remove(key));
	}

	protected void importPropertyMapEntry(Map.Entry<Block, ? extends BlockPropertyMap> entry)
	{
		this.map.computeIfAbsent(entry.getKey(), MutableBlockPropertyMap::new).importProperties(entry.getValue());
	}

	@Override
	protected Map<Block, ? extends BlockPropertyMap> getBlockProperties()
	{
		return this.map;
	}

	public static BlockPropertiesBuilder<MutableBlockProperties> builder()
	{
		return new MutableBlockProperties().editingBuilder();
	}

	public static MutableBlockProperties mutableCopyOf(BlockProperties properties)
	{
		MutableBlockProperties out = new MutableBlockProperties();
		out.importProperties(properties);
		return out;
	}

	private class MutableBlockPropertiesEditor implements BlockPropertiesBuilder<MutableBlockProperties>
	{
		@Override
		public void put(Block block, String key, Object value)
		{
			MutableBlockProperties.this.put(block, key, value);
		}

		@Override
		public MutableBlockProperties build()
		{
			return MutableBlockProperties.this;
		}
	}
}
