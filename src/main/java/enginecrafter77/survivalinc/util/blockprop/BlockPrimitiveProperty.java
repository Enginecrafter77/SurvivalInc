package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

import java.util.Map;
import java.util.function.Function;

public class BlockPrimitiveProperty<PROP> implements BlockPropertyHolder<PROP> {
	private final Block block;
	private final PROP property;

	public BlockPrimitiveProperty(Block block, PROP property)
	{
		this.block = block;
		this.property = property;
	}

	@Override
	public PROP getValue()
	{
		return this.property;
	}

	@Override
	public Block getBlock()
	{
		return this.block;
	}

	public Map.Entry<Block, PROP> asMapEntry()
	{
		return new BlockSinglePropertyEntryView();
	}

	public <TYPE> BlockPrimitiveProperty<TYPE> map(Function<PROP, TYPE> mapper)
	{
		return new BlockPrimitiveProperty<TYPE>(this.block, mapper.apply(this.property));
	}

	public <TYPE> BlockPrimitiveProperty<TYPE> as(Class<TYPE> type)
	{
		return this.map(type::cast);
	}

	public BlockPrimitiveProperty<Boolean> asBooleanProperty()
	{
		return this.map(Boolean.class::cast);
	}

	public BlockPrimitiveProperty<Float> asFloatProperty()
	{
		return this.map(Float.class::cast);
	}

	public BlockPrimitiveProperty<Integer> asIntProperty()
	{
		return this.asFloatProperty().map(Float::intValue);
	}

	public BlockPrimitiveProperty<String> asStringProperty()
	{
		return this.map(String.class::cast);
	}

	public boolean asBoolean()
	{
		return this.asBooleanProperty().getValue();
	}

	public float asFloat()
	{
		return this.asFloatProperty().getValue();
	}

	public int asInt()
	{
		return this.asIntProperty().getValue();
	}

	public String asString()
	{
		return this.asStringProperty().getValue();
	}

	public static <FROM, TO> Function<BlockPrimitiveProperty<FROM>, BlockPrimitiveProperty<TO>> mappingFunction(Function<FROM, TO> mapper)
	{
		return (BlockPrimitiveProperty<FROM> prop) -> prop.map(mapper);
	}

	public static <TYPE> Function<BlockPrimitiveProperty<?>, BlockPrimitiveProperty<TYPE>> cast(Class<TYPE> type)
	{
		return (BlockPrimitiveProperty<?> prop) -> prop.as(type);
	}

	public static Function<BlockPrimitiveProperty<Object>, BlockPrimitiveProperty<Boolean>> castBoolean()
	{
		return mappingFunction(Boolean.class::cast);
	}

	public static Function<BlockPrimitiveProperty<Object>, BlockPrimitiveProperty<Float>> castFloat()
	{
		return mappingFunction(Float.class::cast);
	}

	public static Function<BlockPrimitiveProperty<Object>, BlockPrimitiveProperty<String>> castString()
	{
		return mappingFunction(String.class::cast);
	}

	public static <TYPE> BlockPrimitiveProperty<TYPE> of(Block block, TYPE type)
	{
		return new BlockPrimitiveProperty<TYPE>(block, type);
	}

	private class BlockSinglePropertyEntryView implements Map.Entry<Block, PROP>
	{
		@Override
		public Block getKey()
		{
			return BlockPrimitiveProperty.this.block;
		}

		@Override
		public PROP getValue()
		{
			return BlockPrimitiveProperty.this.property;
		}

		@Deprecated
		@Override
		public PROP setValue(PROP value)
		{
			throw new UnsupportedOperationException();
		}
	}
}
