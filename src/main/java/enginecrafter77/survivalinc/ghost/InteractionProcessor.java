package enginecrafter77.survivalinc.ghost;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class InteractionProcessor implements Function<PlayerInteractEvent, Float> {
	
	public final Class<? extends PlayerInteractEvent> eventclass;
	public final Map<Class<? extends Block>, Float> general_cases;
	public final Map<Block, Float> specific_cases;
	public final float flatrate;
	
	public InteractionProcessor(Class<? extends PlayerInteractEvent> eventclass, float flatrate)
	{
		this.specific_cases = new HashMap<Block, Float>();
		this.general_cases = new HashMap<Class<? extends Block>, Float>();
		this.eventclass = eventclass;
		this.flatrate = flatrate;
	}
	
	public void setBlockCost(Block block, float multiplier)
	{
		this.specific_cases.put(block, multiplier);
	}
	
	public void addBlockClass(Class<? extends Block> blockclass, float multiplier)
	{
		this.general_cases.put(blockclass, multiplier);
	}
	
	public IBlockState getBlock(PlayerInteractEvent event)
	{
		return event.getWorld().getBlockState(event.getPos());
	}
	
	@Override
	public Float apply(PlayerInteractEvent target)
	{
		Float rate = null;
		if(target.getClass() == this.eventclass)
		{
			IBlockState blockstate = this.getBlock(target);
			Block block = blockstate.getBlock();
			Class<? extends Block> blockclass = block.getClass();
			
			if(this.specific_cases.containsKey(block))
			{
				rate = this.flatrate * this.specific_cases.get(block);
			}
			else if(this.general_cases.containsKey(blockclass))
			{
				rate = this.flatrate * this.general_cases.get(blockclass);
			}
			else if(ModConfig.GHOST.interactionSubclassing) // This is a compatibility feature (disabled by default)
			{
				for(Map.Entry<Class<? extends Block>, Float> entry : this.general_cases.entrySet())
				{
					if(entry.getKey().isAssignableFrom(blockclass))
					{
						rate = this.flatrate * entry.getValue();
						break;
					}
				}
			}
		}
		return rate;
	}
	
}
