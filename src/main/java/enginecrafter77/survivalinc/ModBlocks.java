package enginecrafter77.survivalinc;

import java.util.function.Supplier;

import enginecrafter77.survivalinc.block.BlockColoredLeaves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public enum ModBlocks implements Supplier<Block> {
	
	RED_LEAVES(new BlockColoredLeaves(BlockPlanks.EnumType.OAK)),
	YELLOW_LEAVES(new BlockColoredLeaves(BlockPlanks.EnumType.BIRCH)),
	ORANGE_LEAVES(new BlockColoredLeaves(BlockPlanks.EnumType.DARK_OAK));
	
	public final Block targetblock;
	
	private ModBlocks(Block instance)
	{
		this.targetblock = instance;
	}

	@Override
	public Block get()
	{
		return this.targetblock;
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> reg = event.getRegistry();
		for(ModBlocks mb : ModBlocks.values())
			reg.register(mb.get());
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> reg = event.getRegistry();
		for(ModBlocks mb : ModBlocks.values())
		{
			Block block = mb.get();
			reg.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}
	}
}