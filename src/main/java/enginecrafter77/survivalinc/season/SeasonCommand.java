package enginecrafter77.survivalinc.season;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
		
		if(args.length < 1) throw new WrongUsageException("Insufficient arguments\nUsgae: " + this.getUsage(sender));
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream message = new PrintStream(buffer);
		switch(args[0])
		{
		case "set":
			if(args.length < 3) throw new WrongUsageException("Insufficient arguments\nUsage: " + this.getUsage(sender));
			date.setSeason(date.getCalendarEntry().getCalendar().getSeason(new ResourceLocation(args[1])));
			date.setDay(Integer.parseInt(args[2]));
			message.format("Set calendar time to %s\n", date.toString());
			data.markDirty();
			break;
		case "advance":
			int days = 1;
			if(args.length >= 2) days = CommandBase.parseInt(args[1]);
			date.advance(days);
			message.format("Advancing season by %d day(s) --> %s\n", days, date.toString());
			data.markDirty();
			break;
		case "info":
			float currentoffset = SeasonController.instance.biomeTemp.getSeasonalTemperatureOffset(date);
			SeasonCalendarDate next = date.clone();
			next.advance(1);
			
			message.format("$aCurrent season:$r %s (Day %d)\n", localizeSeasonName(date.getCalendarEntry()), date.getDay());
			message.format("$aSeason Length:$r %d\n", date.getCalendarEntry().getSeason().getLength());
			message.format("$aTemperature Offset on $eDay %d$a:$r %.03f\n", date.getDay(), currentoffset);
			message.format("$aPeak Temperature Offset in $e%s$a:$r %f\n", localizeSeasonName(date.getCalendarEntry()), date.getCalendarEntry().getSeason().getPeakTemperature());
			message.format("$aCurrent Temperature Inclination:$r %.03f", SeasonController.instance.biomeTemp.getSeasonalTemperatureOffset(next) - currentoffset);
			if(sender instanceof Entity)
			{
				BlockPos position = new BlockPos(sender.getPositionVector());
				Biome biome = server.getWorld(0).getBiome(position);
				float biometempdiff = biome.getTemperature(position) - biome.getDefaultTemperature();
				message.format("\n$aNominal temperature in current biome:$r %.02f ($7%s$r)", SeasonController.instance.biomeTemp.originals.get(biome), SeasonCommand.formatOffset("%.03f", currentoffset));
				message.format("\n$aTemperature at $eX%d Y%d Z%d$a:$r %.02f ($7%s$r)", position.getX(), position.getY(), position.getZ(), biome.getTemperature(position), SeasonCommand.formatOffset("%.03f", biometempdiff));
			}
			break;
		case "list":
			List<SeasonCalendar.SeasonCalendarEntry> entries = date.getCalendarEntry().getCalendar().getSeasons();
			message.print("$aAvailable seasons:$r");
			for(SeasonCalendar.SeasonCalendarEntry entry : entries)
				message.format("\n$e%s$r ($e%s$r): $e%d$r days$r", localizeSeasonName(entry), entry.getSeason().getName().toString(), entry.getSeason().getLength());
			break;
		default:
			// Like that's ever gonna happen! What a load of [sploosh] SOMEBODY ONCE TOLD ME THE WORLD IS GONNA ROLL ME...
			throw new SyntaxErrorException("You can only choose one of the aforementioned options as the first argument!");
		}
		message.close();
		sender.sendMessage(new TextComponentString(buffer.toString().replace('$', '\u00a7'))); // Replace the $ sign with the minecraft formatting sign (Code 00A7)
		
		if(data.isDirty())
		{
			SurvivalInc.proxy.net.sendToDimension(new SeasonSyncMessage(data), DimensionType.OVERWORLD.getId());
			MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent(world, date));
		}
	}
	
	private static String localizeSeasonName(SeasonCalendar.SeasonCalendarEntry entry)
	{
		return I18n.format(entry.getSeason().getTranslationKey(), new Object[0]);
	}
	
	private static <NUM extends Number> String formatOffset(String formatting, NUM number)
	{
		StringBuilder builder = new StringBuilder();
		if(number.floatValue() > 0) builder.append('+');
		builder.append(String.format(formatting, number));
		return builder.toString();
	}
}
