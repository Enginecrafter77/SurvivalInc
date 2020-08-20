package enginecrafter77.survivalinc.season.melting;

import java.util.Collection;

import net.minecraft.world.chunk.Chunk;

/**
 * ChunkFilter represents an interface
 * used to traverse a single vertical
 * layer of the targeted chunk.
 * @author Enginecrafter77
 */
public interface ChunkFilter
{	
	/**
	 * Performs the operation this chunk filter aims
	 * to do on the block position previously relative
	 * to the start of the chunk. That means the position
	 * parameter must be [0, 0, 0] at least and [15, 256, 15]
	 * at most under normal circumstances.
	 * @param chunk The chunk being processed
	 * @param position The block position relative to chunk start
	 */
	public void processChunks(Collection<Chunk> chunks);
}