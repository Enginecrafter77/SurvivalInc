package enginecrafter77.survivalinc.stats;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

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
		return "/stat <player> [list|<get|set> [stat] [value]]";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{		
		if(args.length < 1) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
		
		EntityPlayer player = CommandBase.getPlayer(server, sender, args[0]);
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		
		if(args.length < 2 || args[1].equals("list"))
		{
			ITextComponent component = new TextComponentString(player.getDisplayNameString() + "'s stats:");
			for(StatProvider provider : tracker.getRegisteredProviders())
			{
				component.appendText("\n ");
				component.appendSibling(new TextComponentTranslation(String.format("stat.%s.name", provider.getStatID().toString())).setStyle(this.statNameStyle));
				component.appendText("(" + TextFormatting.YELLOW + provider.getStatID().toString() + TextFormatting.RESET + ")");
				component.appendText(": " + tracker.getRecord(provider).toString());
			}
			sender.sendMessage(component);
		}
		else
		{
			if(args.length < 3) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
			
			ResourceLocation res = new ResourceLocation(args[2]);
			StatProvider provider = tracker.getProvider(res);
			if(provider == null) throw new CommandException("Stat " + res.toString() + " does not exist!");
			
			StatRecord record = tracker.getRecord(provider);
			if(args[1].equals("set"))
			{
				if(record instanceof SimpleStatRecord)
				{
					if(args.length < 4) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
					((SimpleStatRecord)record).setValue(Float.parseFloat(args[3]));
					SurvivalInc.proxy.net.sendToAll(new StatSyncMessage(player));
				}
				else throw new CommandException("Stat " + provider.getStatID().toString() + " uses non-standard record type!");
			}
			else sender.sendMessage(new TextComponentString(provider.getStatID() + ": " + record.toString()));
		}
	}
	
}
