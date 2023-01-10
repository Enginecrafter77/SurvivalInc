package enginecrafter77.survivalinc.stats.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enginecrafter77.survivalinc.util.ExportedResource;
import enginecrafter77.survivalinc.util.FunctionalImplementation;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SanityBlockEffectMap {
	private final Map<Block, Float> sanityEffectMap;

	public SanityBlockEffectMap()
	{
		this.sanityEffectMap = new HashMap<Block, Float>();
	}

	public void register(Block block, Float value)
	{
		this.sanityEffectMap.put(block, value);
	}

	public float getBlockCoreSanityEffect(Block block)
	{
		return this.sanityEffectMap.getOrDefault(block, 0F);
	}

	public float getBlockEffectAtDistance(Block block, float distance)
	{
		return this.getBlockCoreSanityEffect(block) * this.getDistanceScaleFactor(distance);
	}

	public float getDistanceScaleFactor(float distance)
	{
		return 0.05F / (1F + (float)Math.pow(distance, 2F));
	}

	public float calculateSurroundingsSanityEffect(IBlockAccess world, Vec3d position, float scanDistance)
	{
		Vec3i offset = new Vec3i(scanDistance, 1, scanDistance);
		BlockPos originblock = new BlockPos(position);
		Iterable<BlockPos.MutableBlockPos> blocks = BlockPos.getAllInBoxMutable(originblock.subtract(offset), originblock.add(offset));

		float effect = 0F;
		for(BlockPos blockPos : blocks)
		{
			Block block = world.getBlockState(blockPos).getBlock();
			float distance = (float)Math.sqrt(blockPos.distanceSqToCenter(position.x, position.y, position.z));
			effect += this.getBlockEffectAtDistance(block, distance);
		}
		return effect;
	}

	@FunctionalImplementation(of = ExportedResource.ResourceConsumer.class)
	public void loadFrom(InputStream input)
	{
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(new InputStreamReader(input));

		JsonObject rootMap = root.getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry : rootMap.entrySet())
		{
			Block block = Block.getBlockFromName(entry.getKey());
			if(block == null)
				continue;
			float value = entry.getValue().getAsFloat();
			this.register(block, value);
		}
	}
}
