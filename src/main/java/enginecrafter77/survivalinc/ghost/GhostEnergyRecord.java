package enginecrafter77.survivalinc.ghost;

import com.google.common.collect.Range;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.nbt.NBTTagCompound;

public class GhostEnergyRecord extends SimpleStatRecord {
	public static final String[] STATUS_DESCRIPTIONS = new String[] {"INACTIVE", "ACTIVE", "DEACTIVATING", "ACTIVATING"};
	
	/**
	 * BITS:
	 * 0: active
	 * 1: changing
	 */
	private byte status;
	
	/** The resurrection progress. */
	private int resurrection_progress;
	
	public GhostEnergyRecord()
	{
		super(Range.closed(0F, 100F));
		this.status = 0x0; // INACTIVE
		this.resurrection_progress = -1;
	}
	
	/**
	 * Essentially, this method returns true until the ghost mode is fully deactivated.
	 * @return True if the ghost should receive any ticks.
	 */
	public boolean shouldReceiveTicks()
	{
		return this.status != 0;
	}
	
	/**
	 * @return True if the ghost mode is at least partly activated.
	 */
	public boolean isActive()
	{
		return (this.status & 0x1) > 0;
	}
	
	/**
	 * Sets the current status of the ghost. The status is initially
	 * set to intermediate state. This state needs to be accepted using
	 * {@link #acceptChange()}.
	 * @see #hasPendingChange()
	 * @param active Whether the ghost mode should be enabled
	 */
	public void setActive(boolean active)
	{
		this.status &= 0xFE;
		if(active) this.status |= 0x1;
		this.status |= 0x2;
		SurvivalInc.logger.info("Setting ghost status {}.", this.getStatus());
	}
	
	/**
	 * @return True if the ghost is in transitional state between two states
	 */
	public boolean hasPendingChange()
	{
		return (this.status & 0x2) > 0;
	}
	
	/**
	 * Accepts the status change, causing the ghost to go into fully functional specified mode.
	 */
	public void acceptChange()
	{
		this.status &= 0xFD;
		SurvivalInc.logger.info("Ghost status change to {} accepted.", this.getStatus());
	}
	
	/**
	 * @return The textual description of the current status.
	 */
	public String getStatus()
	{
		return GhostEnergyRecord.STATUS_DESCRIPTIONS[this.status];
	}
	
	/**
	 * @return True if the resurrection is ready to be performed
	 */
	public boolean isResurrectionReady()
	{
		return this.resurrection_progress >= ModConfig.GHOST.resurrectionDuration;
	}
	
	/**
	 * @return True if the resurrection has been started (and not finished yet)
	 */
	public boolean isResurrectionActive()
	{
		return this.resurrection_progress > -1;
	}
	
	/**
	 * @return The remaining ticks until resurrection is ready
	 */
	public int timeUntilResurrection()
	{
		return ModConfig.GHOST.resurrectionDuration - this.resurrection_progress;
	}
	
	/**
	 * Returns the fraction of the required progress to perform resurrection.
	 * @return A value equal to fraction of the required resurrection progress.
	 */
	public float getResurrectionProgress()
	{
		return (float)this.resurrection_progress / (float)ModConfig.GHOST.resurrectionDuration;
	}
	
	/**
	 * Finishes the resurrection and transforms the ghost into a normal player.
	 * The resurrection progress is reset back to 0
	 */
	public void finishResurrection()
	{
		this.setValue(0F);
		this.setActive(false);
		this.resurrection_progress = -1;
	}
	
	/**
	 * Adds one tick to the resurrection progress.
	 * If the resurrection is stopped, this method
	 * starts the resurrection proccess.
	 */
	public void tickResurrection()
	{
		this.resurrection_progress++;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = super.serializeNBT();
		tag.setBoolean("active", this.isActive());
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		super.deserializeNBT(nbt);
		this.setActive(nbt.getBoolean("active"));
	}
	
}
