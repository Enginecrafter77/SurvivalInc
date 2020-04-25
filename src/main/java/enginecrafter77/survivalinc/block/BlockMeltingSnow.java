package enginecrafter77.survivalinc.block;

import java.util.Random;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class BlockMeltingSnow extends Block {

	public static final PropertyInteger MELTPHASE = PropertyInteger.create("meltphase", 0, 3);
	
	public static final BlockMeltingSnow instance = new BlockMeltingSnow();
	
	public BlockMeltingSnow()
	{
		super(Material.SNOW);//BlockSnow
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "melting_snow"));
		this.setDefaultState(this.blockState.getBaseState().withProperty(MELTPHASE, Integer.valueOf(0)));
		this.setCreativeTab(SurvivalInc.mainTab);
		this.setTranslationKey("melting_snow");
		this.setTickRandomly(true);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
	}
	
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return super.isPassable(worldIn, pos);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return this.getBoundingBox(blockState, worldIn, pos);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		int phase = state.getValue(MELTPHASE);
		Item ret = null;
		switch(phase)
		{
		case 1:
			if(rand.nextBoolean()) break; // If true, you won't get anything.
		case 0:
			ret = Items.SNOWBALL;
		}
		return ret;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {MELTPHASE});
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(MELTPHASE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(MELTPHASE, meta);
	}

	@Override
	public void updateTick(World world, BlockPos position, IBlockState state, Random rng)
	{
		int phase = state.getValue(MELTPHASE) + 1;
		SurvivalInc.logger.info("Triggering snow melt. Current level: {}", phase);
		if(MELTPHASE.getAllowedValues().contains(phase))
		{
			state = state.withProperty(MELTPHASE, phase);
			world.setBlockState(position, state, 3);
		}
		else world.setBlockToAir(position);
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(BlockMeltingSnow.instance);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemBlock(BlockMeltingSnow.instance).setRegistryName(BlockMeltingSnow.instance.getRegistryName()));
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(instance), 0, new ModelResourceLocation(instance.getRegistryName(), "inventory"));
	}
	
}
