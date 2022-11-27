package enginecrafter77.survivalinc.stats;

import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.util.FormattedTextComponent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.List;

/**
 * Command used to control the stats for a player.
 * @author Enginecrafter77
 */
public class StatCommand extends CommandBase {
	
	private final Style statNameStyle;
	
	public StatCommand()
	{
		this.statNameStyle = new Style().setColor(TextFormatting.GREEN);
	}
	
	@Override
	public String getName()
	{
		return "stat";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/stat <player> [list|sync|<get|set> [stat] [value]]";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		this.assertArguments(args, sender, 1);
		List<EntityPlayerMP> affected = CommandBase.getPlayers(server, sender, args[0]);
		for(EntityPlayerMP player : affected)
			this.applyTo(player, sender, args);
	}
	
	public void applyTo(EntityPlayer player, ICommandSender sender, String[] args) throws CommandException
	{
		StatTracker tracker = player.getCapability(StatCapability.target, null);

		if(tracker == null)
			throw new CommandException("command.stats.error.trackerNotAvailable");

		String cmd = args.length > 1 ? args[1] : "list";
		switch(cmd)
		{
		case "list":
			ITextComponent component = new TextComponentString(player.getDisplayNameString() + "'s stats:");
			for(StatProvider<?> provider : tracker.getRegisteredProviders())
			{
				component.appendText("\n ");
				component.appendSibling(new TextComponentTranslation(String.format("stat.%s.name", provider.getStatID())).setStyle(this.statNameStyle));
				component.appendSibling(new FormattedTextComponent("(${YELLOW}%s${RESET}): %s", provider.getStatID(), tracker.getRecord(provider)));
			}
			sender.sendMessage(component);
			break;
		case "get":
			this.assertArguments(args, sender, 3);
			ResourceLocation res = new ResourceLocation(args[2]);
			StatProvider<?> provider = tracker.getProvider(res);
			if(provider == null) throw new CommandException("command.stats.error.noSuchProvider", res.toString());
			StatRecord record = tracker.getRecord(provider);
			sender.sendMessage(new TextComponentString(res + ": " + record));
			break;
		case "set":
			this.assertArguments(args, sender, 4);
			res = new ResourceLocation(args[2]);
			provider = tracker.getProvider(res);
			if(provider == null) throw new CommandException("command.stats.error.noSuchProvider", res.toString());
			record = tracker.getRecord(provider);
			
			if(!(record instanceof SimpleStatRecord)) throw new CommandException("command.stats.error.recordNotSimple");
			SimpleStatRecord ssr = (SimpleStatRecord)record;
			
			String value = args[3];
			if(value.startsWith("+") || value.startsWith("-"))
			{
				int operation = value.charAt(0) == '+' ? 1 : -1;
				ssr.addToValue(Float.parseFloat(value.substring(1)) * operation);
			}
			else ssr.setValue(Float.parseFloat(value));
		case "sync":
			StatCapability.synchronizeStats(StatSyncMessage.withPlayer(player));
			break;
		default:
			throw new CommandException("command.stats.error.unknownOperation", cmd, this.getUsage(sender));
		}
	}
	
	public void assertArguments(String[] args, ICommandSender sender, int argc) throws CommandException
	{
		if(args.length < argc) throw new CommandException("command.common.error.insufficientArguments", this.getUsage(sender));
	}
	
}
