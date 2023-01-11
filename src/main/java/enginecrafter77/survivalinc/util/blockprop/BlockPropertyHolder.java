package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

import java.util.function.Predicate;

public interface BlockPropertyHolder<TYPE> {
	public Block getBlock();

	public TYPE getValue();

	public static Predicate<BlockPropertyHolder<?>> byBlock(Block block)
	{
		return (BlockPropertyHolder<?> holder) -> holder.getBlock() == block;
	}
}
