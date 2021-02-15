package enginecrafter77.survivalinc.season;

import java.util.List;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
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
		return "/season <set|info|advance|list> [<season> [day]]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		WorldServer world = server.getWorld(DimensionType.OVERWORLD.getId());
		SeasonData data = SeasonData.load(world);
		SeasonCalendarDate date = data.getCurrentDate();
		SeasonProvider season = date.getCalendarEntry().getSeason();
		
		if(args.length < 1) throw new WrongUsageException("Insufficient arguments\nUsgae: " + this.getUsage(sender));
		
		ITextComponent seasonname = new TextComponentTranslation(season.getTranslationKey());
		
		TextComponentString message = null;
		switch(args[0])
		{
		case "set":
			if(args.length < 3) throw new WrongUsageException("Insufficient arguments\nUsage: " + this.getUsage(sender));
			SeasonCalendar.SeasonCalendarEntry target = date.getCalendarEntry().getCalendar().getSeason(new ResourceLocation(args[1]));
			if(target == null) throw new CommandException("Season \"" + args[1] + "\" not found.");
			date.setSeason(target);
			date.setDay(Integer.parseInt(args[2]));
			message = new TextComponentString(String.format("Set calendar time to %s", date.toString()));
			data.markDirty();
			break;
		case "advance":
			int days = 1;
			if(args.length >= 2) days = CommandBase.parseInt(args[1]);
			date.advance(days);
			message = new TextComponentString(String.format("Advancing season by %d day(s) --> %s", days, date.toString()));
			data.markDirty();
			break;
		case "info":
			float currentoffset = SeasonController.instance.biomeTemp.getSeasonalTemperatureOffset(date);
			SeasonCalendarDate next = date.clone();
			next.advance(1);			
			message = new TextComponentString("§aCurrent season§r: ");
			message.appendSibling(seasonname);
			message.appendText(String.format(" (Day %d)\n§aSeason Length§r: %d\n§aTemperature Offset on §eDay %1$d§r: %.03f\n§aPeak Temperature Offset in §e", date.getDay(), season.getLength(), currentoffset));
			message.appendSibling(seasonname);
			message.appendText(String.format("§r: %f\n§aCurrent Temperature Inclination§r: %.03f", season.getPeakTemperature(), SeasonController.instance.biomeTemp.getSeasonalTemperatureOffset(next) - currentoffset));
			if(sender instanceof Entity)
			{
				BlockPos position = new BlockPos(sender.getPositionVector());
				Biome biome = server.getWorld(0).getBiome(position);
				float biometempdiff = biome.getTemperature(position) - biome.getDefaultTemperature();
				message.appendText(String.format("\n§aNominal temperature in current biome§r: %.02f (§7%s§r)\n§aTemperature at §eX%d Y%d Z%d§r: %.02f (§7%s§r)", SeasonController.instance.biomeTemp.originals.get(biome), SeasonCommand.formatOffset("%.03f", currentoffset), position.getX(), position.getY(), position.getZ(), biome.getTemperature(position), SeasonCommand.formatOffset("%.03f", biometempdiff)));
			}
			break;
		case "list":
			List<SeasonCalendar.SeasonCalendarEntry> entries = date.getCalendarEntry().getCalendar().getSeasons();
			message = new TextComponentString("§aAvailable seasons:§r");
			for(SeasonCalendar.SeasonCalendarEntry entry : entries)
			{
				message.appendText("\n§e");
				message.appendSibling(seasonname);
				message.appendText(String.format("§r (§e%s§r): §e%d§r days", entry.getSeason().getName().toString(), entry.getSeason().getLength()));
			}
			break;
		default:
			// Like that's ever gonna happen! What a load of [sploosh] SOMEBODY ONCE TOLD ME THE WORLD IS GONNA ROLL ME...
			throw new SyntaxErrorException("You can only choose one of the aforementioned options as the first argument!");
		}
		sender.sendMessage(message);
		
		if(data.isDirty())
		{
			SurvivalInc.proxy.net.sendToDimension(new SeasonSyncMessage(data), DimensionType.OVERWORLD.getId());
			MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent(world, date));
		}
	}
	
	private static <NUM extends Number> String formatOffset(String formatting, NUM number)
	{
		StringBuilder builder = new StringBuilder();
		if(number.floatValue() > 0) builder.append('+');
		builder.append(String.format(formatting, number));
		return builder.toString();
	}
}
