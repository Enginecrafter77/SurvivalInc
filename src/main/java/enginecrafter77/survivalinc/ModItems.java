package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.item.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public enum ModItems {
	
	CANTEEN(new ItemCanteen(), "canteen"),
	FEATHER_FAN(new ItemFeatherFan(), "feather_fan"),
	TOWEL(new ItemTowel(), "towel_dry", "towel_wet");
	
	public final Item target;
	public final String[] models;
	public final int[] mappings;
	
	private ModItems(Item instance, int[] mappings, String... models)
	{
		this.target = instance;
		this.models = models;
		this.mappings = mappings;
	}
	
	private ModItems(Item instance, String... models)
	{
		this(instance, new int[0], models);
	}
	
	public Item getItem()
	{
		return this.target;
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> reg = event.getRegistry();
		for(ModItems mi : ModItems.values())
			reg.register(mi.getItem());
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		for(ModItems entry : ModItems.values())
		{
			Item item = entry.getItem();
			int index = 0, meta;
			
			for(String model : entry.models)
			{
				meta = index;
				if(entry.mappings.length > index)
					meta = entry.mappings[index];
				ResourceLocation loc = new ResourceLocation(SurvivalInc.MOD_ID, model);
				ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(loc, "inventory"));
				SurvivalInc.logger.info("Registering model {} on meta {} for item {}", loc.toString(), meta, item.getRegistryName().toString());
				index++;
			}
		}
	}
}