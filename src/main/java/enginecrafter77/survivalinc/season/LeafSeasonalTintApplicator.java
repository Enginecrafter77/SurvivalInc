package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.config.ModConfig;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class LeafSeasonalTintApplicator implements IBlockColor {
	public static final LeafSeasonalTintApplicator INSTANCE = new LeafSeasonalTintApplicator();
	
	/** The tree types that should not lose their leaves */
	public final Set<BlockPlanks.EnumType> persistentTypes;
	
	public LeafSeasonalTintApplicator()
	{
		this.persistentTypes = new HashSet<BlockPlanks.EnumType>();
	}
	
	@Override
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess accessor, @Nullable BlockPos position, int tint)
	{
		BlockPlanks.EnumType treetype = state.getValue(BlockOldLeaf.VARIANT);
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
		
		WorldClient world = Minecraft.getMinecraft().world;
		if(!this.persistentTypes.contains(treetype) && world != null)
		{
			AbstractSeason ssn = SeasonData.load(world).getCurrentDate().getCalendarBoundSeason().getSeason();
			
			if(ssn.getPeakTemperature() < 0F)
			{
				int component = 0;
				for(double multiplier : ModConfig.CLIENT.autumnLeafColor)
					rgb = multiplyColor(rgb, component++, (float)multiplier);
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
	public void registerBlockColors(ColorHandlerEvent.Block event)
	{
		event.getBlockColors().registerBlockColorHandler(this, Blocks.LEAVES);
		this.persistentTypes.add(BlockPlanks.EnumType.SPRUCE);
	}

}
