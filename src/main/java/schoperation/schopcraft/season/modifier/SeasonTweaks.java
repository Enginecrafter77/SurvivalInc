package schoperation.schopcraft.season.modifier;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.schoperation.schopcraft.season.Season;

import java.util.Iterator;
import java.util.List;

public class SeasonTweaks {
	
	/*
	 * This class contains some miscellaneous season crap.
	 * Crap that doesn't need its own class, nor would it be nice cluttering WorldSeason.class 
	 */
	
	/*
	 * These variables and this method changes grass colors as the seasons progress.
	 */
	// Grass colors.
	private final int SUMMER_GRASS_COLOR = 13296206;
	private final int AUTUMN_GRASS_COLOR = 13925888;
	private final int WINTER_GRASS_COLOR = 6008466;
	
	public int getSeasonGrassColor(Season season, Biome biome) {
		
		// Get temperature
		float temp = biome.getDefaultTemperature();
		
		// Determine what season it is
		if (season == Season.SUMMER) {
			
			// Is the temperature above 0.8? We'll change its grass color then.
			if (temp >= 0.80f) {
				
				return SUMMER_GRASS_COLOR;
			}
			
			else {
				
				return 0;
			}
		}
		
		else if (season == Season.AUTUMN) {
			
			// BELOW 0.8?
			if (temp <= 0.80f) {
					
				return AUTUMN_GRASS_COLOR;
			}
			
			else {
				
				return 0;
			}
		}
		
		else if (season == Season.WINTER) {
			
			// BELOW 0.5?
			if (temp <= 0.50f) {
					
				return WINTER_GRASS_COLOR;
			}
			
			else {
				
				return 0;
			}
		}
		
		else {
			
			return 0;
		}
	}
	
	/*
	 * Make stuff grow slower in winter, and faster in summer.
	 * This will technically affect fire spread and redstone ore updates, but
	 * it'll also affect almost all plant growth: crops, vines, trees, etc. So I'll stick with the gamerule for now.
	 */
	
	public void affectPlantGrowth(World world, Season season) {
		
		// Gamerules
		GameRules gamerules = world.getGameRules();
		
		// Now... what's the season?
		if (season == Season.WINTER) {
			
			gamerules.setOrCreateGameRule("randomTickSpeed", "1");
		}
		
		else if (season == Season.SUMMER) {
			
			gamerules.setOrCreateGameRule("randomTickSpeed", "4");
		}
		
		else {
			
			gamerules.setOrCreateGameRule("randomTickSpeed", "3");
		}
	}
	
	/*
	 * Bonus crop harvests in Autumn
	 */
	
	public List<ItemStack> addBonusHarvest(List<ItemStack> drops) {
		
		// From crops, there can be two items that can drop: the crop, and the seed.
		ItemStack dummy = new ItemStack(Items.ACACIA_BOAT);
		
		ItemStack stack1 = dummy;
		int stack1Count = 0;
		
		ItemStack stack2 = dummy;
		int stack2Count = 0;
		
		// Iterate
		Iterator<ItemStack> iterator = drops.iterator();
		
		
		while (iterator.hasNext()) {
			
			ItemStack selected = iterator.next();
			
			if (selected.areItemStacksEqual(selected, stack1)) {
				
				stack1Count++;
			}
			
			else if (selected.areItemStacksEqual(selected, stack2)) {
				
				stack2Count++;
			}
			
			else if (!selected.areItemStacksEqual(selected, stack1) && stack1.areItemStacksEqual(stack1, dummy)) {
				
				stack1 = selected;
				stack1Count++;
			}
			
			else {
				
				stack2 = selected;
				stack2Count++;
			}
		}
		
		// Alright. We got how many of each itemstack in the list.
		// Let add those amounts. So, we're doubling the harvests.
		
		if (!stack1.areItemStacksEqual(stack1, dummy)) {
			
			for (int i = 0; i < stack1Count; i++) {
				
				drops.add(stack1);
			}
		}
		
		if (!stack2.areItemStacksEqual(stack2, dummy)) {
			
			for (int i = 0; i < stack2Count; i++) {
				
				drops.add(stack2);
			}
		}

		return drops;
	}
}