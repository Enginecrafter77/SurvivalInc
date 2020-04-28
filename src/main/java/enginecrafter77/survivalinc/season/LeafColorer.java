package enginecrafter77.survivalinc.season;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class LeafColorer implements IBlockColor {
	
	/** The tree types that should not lose their leaves */
	public static final Set<BlockPlanks.EnumType> persistentTypes = new HashSet<BlockPlanks.EnumType>();
	
	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess accessor, BlockPos position, int tint)
	{
		BlockPlanks.EnumType treetype = (BlockPlanks.EnumType)state.getValue(BlockOldLeaf.VARIANT);
		int rgb;
		
		switch(treetype)
		{
		case SPRUCE:
			rgb = ColorizerFoliage.getFoliageColorPine();
			break;
		case BIRCH:
			rgb = ColorizerFoliage.getFoliageColorBirch();
			break;
		default:
			if(accessor == null || position == null) rgb = ColorizerFoliage.getFoliageColorBasic();
			else rgb = BiomeColorHelper.getFoliageColorAtPos(accessor, position);
			break;
		}
		
		if(!LeafColorer.persistentTypes.contains(treetype))
		{
			WorldClient world = Minecraft.getMinecraft().world;
			Season ssn = SeasonData.load(world).season;
			
			switch(ssn)
			{
			case WINTER:
			case AUTUMN:
				rgb = multiplyColor(rgb, 0, 1.2F);
				rgb = multiplyColor(rgb, 1, 0.4F);
				rgb = multiplyColor(rgb, 2, 0.6F);
				break;
			default:
				break;
			}
		}
		
		return rgb;
	}
	
	public static int multiplyColor(int color, int component, float ratio)
	{
		component = (2 - component) * 8;
		
		float colorvalue = ((color >> component) & 255) / 255F;
		colorvalue *= ratio;
		
		color &= ~(0xFF << component); // Clear the green color
		color |= (int)(colorvalue * 255F) << component; // Insert the new green color
		
		return color;
	}
	
	@SubscribeEvent
	public static void registerBlockColors(ColorHandlerEvent.Block event)
	{
		event.getBlockColors().registerBlockColorHandler(new LeafColorer(), Blocks.LEAVES);
		LeafColorer.persistentTypes.add(BlockPlanks.EnumType.SPRUCE);
	}

}
