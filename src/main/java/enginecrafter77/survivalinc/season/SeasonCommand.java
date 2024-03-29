package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.season.calendar.CalendarBoundSeason;
import enginecrafter77.survivalinc.season.calendar.MutableSeasonCalendarDate;
import enginecrafter77.survivalinc.season.calendar.SeasonCalendarDate;
import enginecrafter77.survivalinc.util.FormattedTextComponent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class SeasonCommand extends CommandBase {
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
		MutableSeasonCalendarDate date = data.getCurrentDate();
		AbstractSeason season = date.getCalendarBoundSeason().getSeason();
		
		if(args.length < 1) throw new WrongUsageException(this.getUsage(sender));
		
		ITextComponent seasonname = new TextComponentTranslation(season.getTranslationKey());
		
		ITextComponent message;
		switch(args[0])
		{
		case "set":
			if(args.length < 3) throw new WrongUsageException(this.getUsage(sender));
			CalendarBoundSeason target = date.getCalendarBoundSeason().getOwningCalendar().findSeason(new ResourceLocation(args[1]));
			if(target == null)
				throw new CommandException("command.season.error.seasonNotFound", args[1]);
			date.setSeason(target);
			date.setDay(Integer.parseInt(args[2]));
			message = new TextComponentString(String.format("Set calendar time to %s", date));
			data.markDirty();
			break;
		case "advance":
			int days = 1;
			if(args.length >= 2) days = CommandBase.parseInt(args[1]);
			date.advance(days);
			message = new TextComponentString(String.format("Advancing season by %d day(s) --> %s", days, date));
			data.markDirty();
			break;
		case "info":
			float currentoffset = SurvivalInc.seasonController.getSeasonalTemperatureOffset(date);
			SeasonCalendarDate next = date.toImmutable().afterDays(1);
			message = new FormattedTextComponent("${GREEN}Current season${RESET}: ");
			message.appendSibling(seasonname);
			message.appendSibling(new FormattedTextComponent(" (Day %d)\n${GREEN}Season Length${RESET}: %d\n${GREEN}Temperature Offset on ${YELLOW}Day %1$d${RESET}: %.03f\n${GREEN}Peak Temperature Offset in ${YELLOW}", date.getDay(), season.getLength(), currentoffset));
			message.appendSibling(seasonname);
			message.appendSibling(new FormattedTextComponent("${RESET}: %f\n${GREEN}Current Temperature Inclination${RESET}: %.03f", season.getPeakTemperature(), SurvivalInc.seasonController.getSeasonalTemperatureOffset(next) - currentoffset));
			if(sender instanceof Entity)
			{
				BlockPos position = new BlockPos(sender.getPositionVector());
				Biome biome = server.getWorld(0).getBiome(position);
				float biometempdiff = biome.getTemperature(position) - biome.getDefaultTemperature();
				float originaltemp = SurvivalInc.seasonController.getBiomeTemperatureInjector().getOriginalBiomeTemperature(biome);
				message.appendSibling(new FormattedTextComponent("\n${GREEN}Nominal temperature in current biome${RESET}: %.02f (${GRAY}%+f${RESET})\n${GREEN}Temperature at ${YELLOW}X%d Y%d Z%d${RESET}: %.02f (${GRAY}%+f${RESET})", originaltemp, currentoffset, position.getX(), position.getY(), position.getZ(), biome.getTemperature(position), biometempdiff));
			}
			break;
		case "list":
			List<? extends CalendarBoundSeason> entries = date.getCalendarBoundSeason().getOwningCalendar().getSeasons();
			message = new FormattedTextComponent("${GREEN}Available seasons:${RESET}");
			for(CalendarBoundSeason entry : entries)
			{
				message.appendText("\n" + TextFormatting.YELLOW);
				message.appendSibling(new TextComponentTranslation(entry.getSeason().getTranslationKey()));
				message.appendSibling(new FormattedTextComponent("${RESET} (${YELLOW}%s${RESET}): ${YELLOW}%d${RESET} days", entry.getSeason().getId().toString(), entry.getSeason().getLength()));
			}
			break;
		default:
			// Like that's ever gonna happen! What a load of [sploosh] SOMEBODY ONCE TOLD ME THE WORLD IS GONNA ROLL ME...
			throw new WrongUsageException(this.getUsage(sender));
		}
		sender.sendMessage(message);
		
		if(data.isDirty())
		{
			SurvivalInc.net.sendToDimension(new SeasonSyncMessage(data), DimensionType.OVERWORLD.getId());
			MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent(world, date));
		}
	}
}
