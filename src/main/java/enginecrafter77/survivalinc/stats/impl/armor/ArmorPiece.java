package enginecrafter77.survivalinc.stats.impl.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ArmorPiece is an enum describing different parts of player armor. The enum may be considered redundant by some since
 * {@link EntityEquipmentSlot} already exists, but this enum aims to connect the player-understood names of armor pieces
 * to their slots. Therefore, the {@link #valueOf(String)} method may be used to conveniently refer to the individual
 * armor slots.
 * @author Enginecrafter77
 */
public enum ArmorPiece {
	HELMET(EntityEquipmentSlot.HEAD),
	CHESTPLATE(EntityEquipmentSlot.CHEST),
	LEGGINGS(EntityEquipmentSlot.LEGS),
	BOOTS(EntityEquipmentSlot.FEET);
	
	/** The associated equipment slot */
	public final EntityEquipmentSlot slot;
	
	private ArmorPiece(EntityEquipmentSlot slot)
	{
		this.slot = slot;
	}
	
	/**
	 * Returns the item stack in the associated slot inside of the provided player's inventory.
	 * @param inventory Player's inventory
	 * @return The {@link ItemStack} contained in the associated armor slot.
	 */
	public ItemStack getPieceStack(InventoryPlayer inventory)
	{
		return inventory.armorInventory.get(this.slot.getIndex());
	}
	
	/**
	 * Creates a wrapper object which allows for convenient iteration of player's armor inventory.
	 * @param player The player whose armor inventory should be iterated.
	 * @return The
	 */
	public static Iterable<Map.Entry<ArmorPiece, ItemStack>> armorInventory(EntityPlayer player)
	{
		return new PlayerInventoryIterableWrapper(player.inventory);
	}
	
	/**
	 * A convenience method for wrapping {@link InventoryPlayer} for the purpose of implementing {@link Iterable} interface
	 * based around {@link InventoryArmorIterator}.
	 * @author Enginecrafter77
	 */
	private static class PlayerInventoryIterableWrapper implements Iterable<Map.Entry<ArmorPiece, ItemStack>> {
		/** The player's inventory */
		public final InventoryPlayer inv;
		
		public PlayerInventoryIterableWrapper(InventoryPlayer inv)
		{
			this.inv = inv;
		}
		
		@Override
		public Iterator<Entry<ArmorPiece, ItemStack>> iterator()
		{
			return new InventoryArmorIterator(this.inv);
		}
	}
	
	/**
	 * InventoryArmorIterator is a zipping iterator that iterates over constants of {@link ArmorPiece} enums and pairs them
	 * up with the {@link ItemStack} instances contained in slots of the associated ArmorPiece instances. This allows for
	 * convenient iteration over player's inventory with regards for armor slot and type.
	 * @author Enginecrafter77
	 */
	public static class InventoryArmorIterator implements Iterator<Map.Entry<ArmorPiece, ItemStack>> {
		/** The player's inventory */
		public final InventoryPlayer playerinv;
		
		/** An iterator of {@link ArmorPiece} enum. */
		private final Iterator<ArmorPiece> pieceitr;
		
		public InventoryArmorIterator(InventoryPlayer inv)
		{
			this.pieceitr = Arrays.stream(ArmorPiece.values()).iterator();
			this.playerinv = inv;
		}
		
		@Override
		public boolean hasNext()
		{
			return this.pieceitr.hasNext();
		}
		
		@Override
		public Entry<ArmorPiece, ItemStack> next()
		{
			ArmorPiece piece = this.pieceitr.next();
			ItemStack stack = piece.getPieceStack(this.playerinv);
			return new AbstractMap.SimpleEntry<ArmorPiece, ItemStack>(piece, stack);
		}
		
	}
}
