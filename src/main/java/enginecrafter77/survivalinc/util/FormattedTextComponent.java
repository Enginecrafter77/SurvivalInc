package enginecrafter77.survivalinc.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattedTextComponent extends TextComponentBase {
	private static final Pattern MACRO_REGEX = Pattern.compile("\\$\\{([^\\s{}]+)}");
	
	public final String formatString;
	private final Object[] formatArgs;
	
	private String compiled;
	
	public FormattedTextComponent(String text, Object... args)
	{
		this.formatString = text;
		this.formatArgs = args;
	}

	public String getSubstitutedText()
	{
		return String.format(this.formatString, this.formatArgs);
	}

	public String compile()
	{
		String formattedText = this.getSubstitutedText();
		StringBuilder builder = new StringBuilder(formattedText);
		Matcher match = FormattedTextComponent.MACRO_REGEX.matcher(formattedText);
		
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
		if(this.compiled == null)
			this.compiled = this.compile();
		return this.compiled;
	}

	@Override
	public ITextComponent createCopy()
	{
		return new FormattedTextComponent(this.formatString, this.formatArgs);
	}	
}
