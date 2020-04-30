package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class LazyMeltingTransformer extends SimpleMeltingTransformer {

	public LazyMeltingTransformer(BlockMelting block, int level)
	{
		super(block, level);
	}

	@Override
	public void applyToChunk(Chunk chunk, BlockPos position)
	{
		super.applyToChunk(chunk, position);
		
		IBlockState state = chunk.getBlockState(position);
		Block block = state.getBlock();
		World world = chunk.getWorld();
		if(block == this.to && world.rand.nextFloat() > 0.1F)
		{
			block.updateTick(world, chunk.getPos().getBlock(position.getX(), position.getY(), position.getZ()), state, world.rand);
		}
	}

}
