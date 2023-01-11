package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

import java.util.HashMap;
import java.util.Map;

public class MutableBlockPropertyMap extends BlockPropertyMap {
	private final Map<String, BlockPrimitiveProperty<?>> properties;

	public MutableBlockPropertyMap(Block block)
	{
		this(block, new HashMap<String, BlockPrimitiveProperty<?>>());
	}

	protected MutableBlockPropertyMap(Block block, Map<String, BlockPrimitiveProperty<?>> props)
	{
		super(block);
		this.properties = props;
	}

	public boolean isEmpty()
	{
		return this.properties.isEmpty();
	}

	public void remove(String key)
	{
		this.properties.remove(key);
	}

	public void put(String key, Object value)
	{
		if(this.isSingular() && !key.equals(KEY_SINGULAR_PROPERTY))
			throw new IllegalArgumentException("Cannot insert custom key into a singular property map!");
		this.properties.put(key, new BlockPrimitiveProperty<Object>(this.getBlock(), value));
	}

	public void importProperties(BlockPropertyMap map)
	{
		if(this.isSingular() != map.isSingular())
			throw new IllegalArgumentException("Cannot import non-singular property map into a singular one!");
		this.properties.putAll(map.getValue());
	}

	@Override
	public Map<String, BlockPrimitiveProperty<?>> getValue()
	{
		return this.properties;
	}

	public static MutableBlockPropertyMap from(Block block, Map<String, Object> props)
	{
		MutableBlockPropertyMap map = new MutableBlockPropertyMap(block);
		props.forEach(map::put);
		return map;
	}
}
