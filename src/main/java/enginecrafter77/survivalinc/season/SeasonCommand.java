package enginecrafter77.survivalinc.season;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class SeasonCommand extends CommandBase {

	public SeasonCommand()
	{
		System.out.println("Registering command season");
	}
	
	@Override
	public String getName()
	{
		return "season";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/season <set|info> [season|day]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		World world = sender.getEntityWorld();
		SeasonData data = SeasonData.load(world);
		
		if(args.length < 1) throw new CommandException("Insufficient arguments\n" + this.getUsage(sender));
		
		switch(args[0])
		{
		case "set":
			if(args.length < 3) throw new CommandException("Missing season name\n" + this.getUsage(sender));
			data.season = Season.valueOf(args[1]);
			data.day = Integer.parseInt(args[2]);
			sender.sendMessage(new TextComponentString("Set season to " + data.toString()));
			MinecraftForge.EVENT_BUS.post(new SeasonUpdateEvent(world, data));
			data.markDirty();
			break;
		case "info":
			float currentoffset = data.season.getTemperatureOffset(data.day);
			TextComponentString text = new TextComponentString("\u00a7aCurrent season: \u00a76" + data.toString());
			text.appendText(String.format("\n\u00a7aBase Temperature:\u00a7r %f", currentoffset));
			text.appendText(String.format("\n\u00a7aPeak Temperature:\u00a7r %f", data.season.getPeakTemperatureOffset()));
			text.appendText(String.format("\n\u00a7aSeason Length:\u00a7r %d", data.season.getLength()));
			text.appendText(String.format("\n\u00a7aTemperature Inclination:\u00a7r %f", data.season.getTemperatureOffset(data.day + 1) - currentoffset));
			if(sender instanceof Entity)
			{
				BlockPos position = new BlockPos(sender.getPositionVector());
				float temperature = server.getWorld(0).getBiome(position).getTemperature(position);
				text.appendText(String.format("\n\u00a7aTemperature at \u00a7e%s:\u00a7r %f", position.toString(), temperature));
			}
			sender.sendMessage(text);
			break;
		default:
			break; // Will not happen
		}
	}
	
}
