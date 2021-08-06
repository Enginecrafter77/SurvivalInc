package enginecrafter77.survivalinc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextFormatting;

public class FormattedTextComponent extends TextComponentBase {
	private static final Pattern formatvar_regex = Pattern.compile("\\$\\{([^\\s\\{\\}]+)\\}");
	
	public final String text;
	
	private String compiled;
	
	public FormattedTextComponent(String text, Object... args)
	{
		this.text = String.format(text, args);
	}
	
	public String compile()
	{
		StringBuilder builder = new StringBuilder(this.text);
		Matcher match = formatvar_regex.matcher(this.text);
		
		int shift = 0;
		while(match.find())
		{
			String name = match.group(1);
			String replacement;
			try
			{
				
				TextFormatting formatting = TextFormatting.valueOf(name);
				replacement = formatting.toString();
			}
			catch(IllegalArgumentException exc)
			{
				replacement = String.format("(%s?)", name);
			}
			builder.replace(match.start() - shift, match.end() - shift, replacement);
			shift += match.group(0).length() - replacement.length();
		}
		
		return builder.toString();
	}
	
	@Override
	public String getUnformattedComponentText()
	{
		if(this.compiled == null) this.compiled = this.compile();
		return this.compiled;
	}

	@Override
	public ITextComponent createCopy()
	{
		return new FormattedTextComponent(this.text, new Object[0]);
	}	
}