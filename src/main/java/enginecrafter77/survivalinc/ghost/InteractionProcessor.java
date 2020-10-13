package enginecrafter77.survivalinc.ghost;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class InteractionProcessor implements Function<PlayerInteractEvent, Float> {
	
	public final Class<? extends PlayerInteractEvent> eventclass;
	public final Map<Block, Map.Entry<Float, Float>> cases;
	public final Set<Block> disabled;
	public final float flatrate;
	
	public InteractionProcessor(Class<? extends PlayerInteractEvent> eventclass, float flatrate)
	{
		this.cases = new HashMap<Block, Map.Entry<Float, Float>>();
		this.disabled = new HashSet<Block>();
		this.eventclass = eventclass;
		this.flatrate = flatrate;
	}
	
	public void mapAbsoluteCase(Block block, float mapping)
	{
		this.mapCase(block, 1F, mapping);
	}
	
	public void mapCase(Block block, float multiplier, float offset)
	{
		this.cases.put(block, new AbstractMap.SimpleEntry<Float, Float>(multiplier, offset));
	}
	
	public void disable(Block block)
	{
		this.disabled.add(block);
	}

	public IBlockState getBlock(PlayerInteractEvent event)
	{
		return event.getEntity().world.getBlockState(event.getPos());
	}
	
	@Override
	public Float apply(PlayerInteractEvent target)
	{
		if(this.eventclass.isAssignableFrom(target.getClass()))
		{
			IBlockState blockstate = this.getBlock(target);
			Block block = blockstate.getBlock();
			
			if(!this.disabled.contains(block))
			{
				if(this.cases.containsKey(block))
				{
					Map.Entry<Float, Float> entry = this.cases.get(block);
					return this.flatrate * entry.getKey() + entry.getValue();
				}
				else return this.flatrate;
			}
		}
		
		return null;
	}
	
}
