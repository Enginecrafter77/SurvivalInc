package enginecrafter77.survivalinc.stats.impl.armor;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArmorConductivityCommand extends CommandBase {
	public static final Pattern DETECTION_BUNDLE_REGEX = Pattern.compile("=([a-zA-Z]+)(?::(.+))?");
	
	public final ConfigurableArmorModifier link;
	
	public ArmorConductivityCommand(ConfigurableArmorModifier link)
	{
		this.link = link;
	}
	
	@Override
	public String getName()
	{
		return "armorconductivity";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return String.format("/%1$s list - Lists the currently effective armor conductivity maps\n/%1$s reload - Reloads the armor conductivity map from the config\n/%1$s save - Saves the currently effective armor conductivity map into the config\n/%1$s set <material> <multiplier> - Sets the armor conductivity multiplier for the given material\n/%1$s reset <material> - Removes the override from the given material\n/%1$s info <material> - Prints information about conductivity of given material", this.getName());
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length == 0) throw new CommandException("Insufficient arguments. Try /" + this.getName() + " help");
		
		// Options not requiring second or third parameter
		switch(args[0])
		{
		case "list":
			TextComponentString response = new TextComponentString("Material list:");
			Iterator<Map.Entry<ItemArmor.ArmorMaterial, Float>> itr = this.link.iterateMap();
			while(itr.hasNext())
			{
				Map.Entry<ItemArmor.ArmorMaterial, Float> mapping = itr.next();
				response.appendText(String.format("\n%s: %.01f", mapping.getKey().name(), mapping.getValue()));
			}
			sender.sendMessage(response);
			return;
		case "reload":
			SurvivalInc.armorConductivityConfig.load(this.link::load);
			sender.sendMessage(new TextComponentString("Reloading configuration..."));
			return;
		case "save":
			try
			{
				FileOutputStream output = new FileOutputStream(SurvivalInc.armorConductivityConfig.getFile());
				this.link.save(output);
				output.close();
				sender.sendMessage(new TextComponentString("Saving configuration..."));
			}
			catch(IOException exc)
			{
				throw new CommandException("command.common.error.ioError");
			}
			return;
		case "help":
			sender.sendMessage(new TextComponentString("Usage: " + this.getUsage(sender)));
			return;
		default:
			break;
		}
		
		if(args.length < 2) throw new CommandException("command.armorConductivity.missingMaterialValue");
		ItemArmor.ArmorMaterial material = this.resolveMaterial(server, sender, args[1]);
		switch(args[0])
		{
		case "set":
			if(args.length < 3) throw new CommandException("command.armorConductivity.missingArmorConductivityValue");
			float value = Float.parseFloat(args[2]);
			this.link.setMaterialConductivity(material, value);
			sender.sendMessage(new TextComponentString(String.format("Set conductivity of %s to %f", material.name(), value)));
			break;
		case "reset":
			this.link.unregisterMaterial(material);
			sender.sendMessage(new TextComponentString(String.format("Removed armor conductivity mapping of %s", material.name())));
			break;
		case "info":
			sender.sendMessage(this.getArmorMaterialInfo(material));
			break;
		default:
			throw new CommandException("command.common.error.invalidOption");
		}
	}
	
	public ITextComponent getArmorMaterialInfo(ItemArmor.ArmorMaterial material)
	{
		Float cdc = this.link.getMaterialBaseConductivity(material);
		
		TextComponentString base = new TextComponentString(String.format("\"%s\": ", material.name()));
		base.appendText(cdc == null ? "Unsupported" : cdc.toString());
		return base;
	}
	
	public ItemArmor.ArmorMaterial resolveMaterial(MinecraftServer server, ICommandSender sender, String materialbundle) throws CommandException
	{
		Matcher match = ArmorConductivityCommand.DETECTION_BUNDLE_REGEX.matcher(materialbundle);
		if(match.matches())
		{
			ArmorPiece piece = ArmorPiece.valueOf(match.group(1).toUpperCase());
			String target = match.group(2);
			EntityPlayer source;
			if(target != null)
			{
				source = CommandBase.getPlayer(server, sender, target);
			}
			else if(sender instanceof EntityPlayer)
			{
				source = (EntityPlayer)sender;
			}
			else
			{
				throw new CommandException("command.common.error.noPlayerSelected");
			}
			ItemStack stack = piece.getPieceStack(source.inventory);
			Item type = stack.getItem();
			if(type instanceof ItemArmor)
			{
				ItemArmor armor = (ItemArmor)type;
				return armor.getArmorMaterial();
			}
			else
			{
				throw new CommandException("Item in armor slot " + piece.toString() + " is not an armor!");
			}
		}
		else
		{
			try
			{
				return ItemArmor.ArmorMaterial.valueOf(materialbundle);
			}
			catch(IllegalArgumentException exc)
			{
				throw new CommandException(String.format("No armor material with name \"%s\" exists!", materialbundle));
			}
		}
	}
	
}
