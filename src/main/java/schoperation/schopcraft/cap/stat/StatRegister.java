package schoperation.schopcraft.cap.stat;

import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import schoperation.schopcraft.SchopCraft;

public class StatRegister implements ICapabilitySerializable<NBTBase> {

	@CapabilityInject(StatTracker.class)
	public static final Capability<StatTracker> CAPABILITY = null;
	private StatTracker instance = CAPABILITY.getDefaultInstance();
	
	private static final ResourceLocation identificator = new ResourceLocation(SchopCraft.MOD_ID, "vitals");

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CAPABILITY;
	}

	@Override
	public <TARGET> TARGET getCapability(Capability<TARGET> capability, EnumFacing facing)
	{
		return this.hasCapability(capability, facing) ? CAPABILITY.cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT()
	{
		return CAPABILITY.getStorage().writeNBT(CAPABILITY, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		CAPABILITY.getStorage().readNBT(CAPABILITY, this.instance, null, nbt);
	}
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if(!(event.getObject() instanceof EntityPlayer)) return;
		
		event.addCapability(StatRegister.identificator, new StatRegister());
	}
	
	public static class Storage implements IStorage<StatTracker> {
		@Override
		public NBTBase writeNBT(Capability<StatTracker> capability, StatTracker instance, EnumFacing side)
		{
			NBTTagCompound compound = new NBTTagCompound();
			for(Entry<StatProvider, Float> entry : instance)
				compound.setFloat(entry.getKey().getStatID(), entry.getValue());
			return compound;
		}

		@Override
		public void readNBT(Capability<StatTracker> capability, StatTracker instance, EnumFacing side, NBTBase nbt)
		{
			if(nbt instanceof NBTTagCompound)
			{
				NBTTagCompound compound = (NBTTagCompound)nbt;
				for(Entry<StatProvider, Float> entry : instance)
				{
					String id = entry.getKey().getStatID();
					if(compound.hasKey(id))
						instance.setStat(entry.getKey(), compound.getFloat(id));
					else
						System.err.format("Error: Requested stat %s not defined in saved NBT!\n", id);
				}
			}
		}
	}
}