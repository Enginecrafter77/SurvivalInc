package enginecrafter77.survivalinc.util.blockprop;

import net.minecraft.block.Block;

public interface MutableBlockPropertyView<VAL> extends BlockPropertyView<VAL> {
	public void setValue(Block block, VAL value);
}
