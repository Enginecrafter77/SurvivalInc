package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

public interface BlockPropertiesBuilder<T extends BlockProperties> {
	public void put(Block block, String key, Object value);
	public T build();
}
