package schoperation.schopcraft.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.lib.ModBlocks;
import net.schoperation.schopcraft.lib.ModItems;
import net.schoperation.schopcraft.lib.ModSounds;

@Mod.EventBusSubscriber
public class Registererer {
	
	/*
	 * This is where all new crap added to the mod is registered (items, blocks, etc.)
	 */
	
	// Register all blocks.
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		
		event.getRegistry().registerAll(ModBlocks.BLOCKS);
	}
	
	// Register all items.
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		
		// Register normal items.
		event.getRegistry().registerAll(ModItems.ITEMS);
		
		// Register itemblocks (items to represent the blocks).
		for (Block block : ModBlocks.BLOCKS) {
			
			event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}
	}
	
	// Register all sounds.
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		
		event.getRegistry().registerAll(ModSounds.SOUNDS);
	}
}