package net.schoperation.schopcraft.season;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.schoperation.schopcraft.SchopCraft;

public class SeasonData extends WorldSavedData {

	// Identifier
	private static final String ID = "schopcraft_season";
	
	// Stuff to save 
	// 1 = winter, 2 = spring, 3 = summer, 4 = autumn
	public int season = 0;
	public int seasonTicks = 0;
	
	// Constructors
	public SeasonData() {
		
		super(ID);
	}
	
	public SeasonData(String id) {
		
		super(id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
		season = nbt.getInteger("season");
		seasonTicks = nbt.getInteger("seasonTicks");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		
		compound.setInteger("season", season);
		compound.setInteger("seasonTicks", seasonTicks);
		
		return compound;
	}
	
	// Easy loading (Do SeasonData seasonData = SeasonData.load(world);)
	public static SeasonData load(World world) {
		
		SeasonData data = (SeasonData) world.getMapStorage().getOrLoadData(SeasonData.class, ID);

		// Does it not exist?
		if (data == null) {
			
			SchopCraft.logger.warn("No season world data found. Creating new file.");
			
			data = new SeasonData();
			
			// Determine starting season
			double springOrFall = Math.random();
			
			if (springOrFall < 0.50) {
				
				data.season = 2;
			}
			
			else {
				
				data.season = 4;
			}
			
			data.seasonTicks = 0;
			
			data.markDirty();
			world.getMapStorage().setData(ID, data);
		}
		
		SchopCraft.logger.info("Loaded season world data. Current season is " + data.getSeasonFromData() + " and seasonTicks is " + data.seasonTicks + ".");
		
		return data;
	}
	
	// Conversion methods
	public static int seasonToInt(Season season) {
		
		switch(season) {
		
			case WINTER: return 1;
			case SPRING: return 2;
			case SUMMER: return 3;
			case AUTUMN: return 4;
			default: return 0;
		}
	}
	
	public Season getSeasonFromData() {
		
		switch(this.season) {
			
			case 1: return Season.WINTER;
			case 2: return Season.SPRING;
			case 3: return Season.SUMMER;
			case 4: return Season.AUTUMN;
			default: return null;
		
		}
	}
}
