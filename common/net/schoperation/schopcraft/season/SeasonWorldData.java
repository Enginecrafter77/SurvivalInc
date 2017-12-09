package net.schoperation.schopcraft.season;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.schoperation.schopcraft.SchopCraft;

public class SeasonWorldData extends WorldSavedData {

	// Identifier
	private static final String ID = "schopcraft_season";
	
	// Stuff to save TODO figure out String season vs Season season
	public String season;
	public int seasonTicks = 0;
	
	// Constructors
	public SeasonWorldData() {
		
		super(ID);
	}
	
	public SeasonWorldData(String id) {
		
		super(id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
		season = nbt.getString("season");
		seasonTicks = nbt.getInteger("seasonTicks");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		
		compound.setString("season", season);
		compound.setInteger("seasonTicks", seasonTicks);
		
		return compound;
	}
	
	// Easy loading (Do SeasonWorldData seasonData = SeasonWorldData.load(world);)
	public static SeasonWorldData load(World world) {
		
		SeasonWorldData data = (SeasonWorldData) world.getMapStorage().getOrLoadData(SeasonWorldData.class, ID);

		// Does it not exist?
		if (data == null) {
			
			SchopCraft.logger.warn("No season world data found. Creating new file.");
			
			data = new SeasonWorldData();
			data.markDirty();
			world.getMapStorage().setData(ID, data);
		}
		
		SchopCraft.logger.info("Loaded season world data. Current season is " + data.season + " and seasonTicks is " + data.seasonTicks + ".");
		
		return data;
	}
}
