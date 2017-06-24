package net.schoperation.schopcraft.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.item.ItemTabIcon;
import net.schoperation.schopcraft.lib.Names;

public class RegAndRen {
	
	/*
	 * The List of stuff - to be honest, I'm not too concerned for "convenience." Everytime I attempt to make something
	 * convenient, it turns out more complex. It's weird. I tried. Feel free to send help if you don't mind.
	 * I'll keep experimenting/copying and pasting from stack overflow.
	 */
	
	// Blocks
	
	
	// Items
	
	public static ItemTabIcon tabIcon = new ItemTabIcon(); // The Tab Icon
	
	/*
	 * End of list of stuff
	 */
	
	
	// used to quickly register all crap in commonproxy without filling up commonproxy unnecessarily
	public static void registerAll() {
		
		// Blocks ###

		
		// Items ###
		registerItem(tabIcon, Names.TAB_ICON);
		
		
	}
	// kinda the same as registerAll(), but for rendering and for clientproxy
	public static void renderAll() {
		
		// dumb mesher... is this needed anymore? i need updated stuff
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		
		// Blocks ###
				
		// Items ###
		renderItem(tabIcon, Names.TAB_ICON, mesher);
		
	}
	// register a block today!
	public static void registerBlock(Block block, String blockname) {
		
		ResourceLocation location = new ResourceLocation(SchopCraft.MOD_ID, blockname);
		block.setRegistryName(location);
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block), location);
		
	}
	// render a block to see it! Client-side only, b/c the server doesn't have eyes... yet
	public static void renderBlock(Block block, String blockname, ItemModelMesher mesher) {
		
		Item item = Item.getItemFromBlock(block);
		ModelResourceLocation model = new ModelResourceLocation(SchopCraft.RESOURCE_PREFIX + blockname, "inventory");
		ModelLoader.registerItemVariants(item, model);
		mesher.register(item, 0, model);
		
	}
	// register an item into the game!
	public static void registerItem(Item item, String itemname) {
		
		item.setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, itemname));
		GameRegistry.register(item);
		
	}
	// render that item into view, and gaze on its very beauty.
	public static void renderItem(Item item, String itemname, ItemModelMesher mesher) {
		
		ModelResourceLocation model = new ModelResourceLocation(SchopCraft.RESOURCE_PREFIX + itemname, "inventory");
		ModelLoader.registerItemVariants(item, model);
		mesher.register(item, 0, model);
		
	}

}
