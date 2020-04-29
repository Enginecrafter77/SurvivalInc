package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

class SimpleMeltingTransformer implements BlockTransformer
{
	public final Block from, to;
	public final float chance;
	
	public SimpleMeltingTransformer(Block from, Block to, float chance)
	{
		this.chance = chance;
		this.from = from;
		this.to = to;
	}
	
	public SimpleMeltingTransformer(Block from, Block to)
	{
		this.chance = 1F;
		this.from = from;
		this.to = to;
	}
	
	@Override
	public IBlockState applyToBlock(Chunk chunk, BlockPos position, IBlockState source)
	{
		if(source.getBlock() == this.from && !BlockMelting.isFreezingAt(chunk, position))
		{
			if(chance < 1F && chunk.getWorld().rand.nextFloat() > chance) return null;
			else return this.to.getDefaultState();
		}
		return source;
	}
}