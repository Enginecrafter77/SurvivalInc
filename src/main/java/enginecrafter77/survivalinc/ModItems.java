package enginecrafter77.survivalinc;

import java.util.function.Supplier;

import enginecrafter77.survivalinc.item.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public enum ModItems implements Supplier<Item> {
	
	CANTEEN(new ItemCanteen()),
	CHARCOAL_FILTER(new ItemCharcoalFilter()),
	FEATHER_FAN(new ItemFeatherFan()),
	ICE_CREAM(new ItemIceCream(4, 0.4f, false)),
	LUCID_DREAM_ESSENCE(new ItemLucidDreamEssence()),
	RESETTER(new ItemResetter()),
	TOWEL(new ItemTowel());
	
	public final Item target;

	private ModItems(Item instance)
	{
		this.target = instance;
	}
	
	@Override
	public Item get()
	{
		return this.target;
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> reg = event.getRegistry();
		for(ModItems mi : ModItems.values())
			reg.register(mi.get());
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		// Register item models.
		for(ModItems mi : ModItems.values())
		{
			Item item = mi.get();
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}
}