package enginecrafter77.survivalinc;

import java.util.function.Supplier;

import enginecrafter77.survivalinc.block.BlockMeltingSnow;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public enum ModBlocks implements Supplier<Block> {
	MELTING_SNOW(new BlockMeltingSnow(true)),
	LAZY_MELTING_SNOW(new BlockMeltingSnow(false));
	
	private final Block instance;
	
	private ModBlocks(Block instance)
	{
		this.instance = instance;
	}
	
	@Override
	public Block get()
	{
		return this.instance;
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> reg = event.getRegistry();
		for(ModBlocks block : ModBlocks.values())
			reg.register(block.instance);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> reg = event.getRegistry();
		for(ModBlocks block : ModBlocks.values())
		{
			Item blockitem = new ItemBlock(block.instance);
			blockitem.setRegistryName(block.instance.getRegistryName());
			reg.register(blockitem);
		}
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		for(ModBlocks block : ModBlocks.values())
		{
			Item blockitem = Item.getItemFromBlock(block.instance);
			ModelLoader.setCustomModelResourceLocation(blockitem, 0, new ModelResourceLocation(blockitem.getRegistryName(), "inventory"));
		}
	}

}
