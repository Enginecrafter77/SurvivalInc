package enginecrafter77.survivalinc.stats.effect.item;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemSituationParser {
	
	private static final ImmutableSet<String> essential_values = ImmutableSet.of("situation", "item", "effects");
	
	public final Map<String, SituationEffectFactory> situations;
	
	public ItemSituationParser()
	{
		this.situations = new HashMap<String, SituationEffectFactory>();
	}
	
	public void addSituationFactory(String name, SituationEffectFactory factory)
	{
		this.situations.put(name, factory);
	}
	
	protected <TYPE> TYPE resolveFromJSON(JsonObject object, String name, Function<String, TYPE> parser, String errormsg) throws IllegalArgumentException, JsonParseException
	{
		try
		{
			String itemname = object.get(name).getAsString();
			TYPE item = parser.apply(itemname);
			if(item == null)
			{
				String msg = String.format(Objects.requireNonNull(errormsg, "Errormsg can't be null!"), itemname);
				throw new IllegalArgumentException(msg);
			}
			return item;
		}
		catch(NullPointerException | UnsupportedOperationException exc)
		{
			throw new JsonParseException(String.format("Element \"%s\" has wrong or missing declaration inside JSON.", name), exc);
		}
	}
	
	protected SituationEffectFactory getFactory(JsonObject object) throws IllegalArgumentException, JsonParseException
	{
		return this.resolveFromJSON(object, "situation", this.situations::get, "Situation %s not recognized by the parser");
	}
	
	protected Item resolveItem(JsonObject object) throws IllegalArgumentException, JsonParseException
	{
		return this.resolveFromJSON(object, "item", Item::getByNameOrId, "Item %s not found.");
	}
	
	protected Properties resolveProperties(JsonObject obj)
	{
		Properties props = new Properties();
		obj.entrySet().stream().filter(this::isProperty).forEach((Map.Entry<String, JsonElement> element) -> {
			if(!element.getValue().isJsonPrimitive()) throw new IllegalArgumentException(String.format("Element property \"%s\" is not JSON primitive!", element.getKey()));
			props.setProperty(element.getKey(), element.getValue().getAsString());
		});
		return props;
	}
	
	private boolean isProperty(Map.Entry<String, JsonElement> entry)
	{
		return !ItemSituationParser.essential_values.contains(entry.getKey());
	}
	
	public Collection<ItemSituation<?>> parse(InputStream source) throws IOException
	{
		LinkedList<ItemSituation<?>> effects = new LinkedList<ItemSituation<?>>();
		
		JsonParser parser = new JsonParser();
		JsonArray index = parser.parse(new InputStreamReader(source)).getAsJsonArray();
		
		for(JsonElement effectjson : index)
		{
			try
			{
				JsonObject effectjsonobj = effectjson.getAsJsonObject();
				
				SituationEffectFactory factory = this.getFactory(effectjsonobj);
				Properties props = this.resolveProperties(effectjsonobj);
				Item item = this.resolveItem(effectjsonobj);
				
				ItemSituation<?> effect = factory.createEffect(item, props);
				
				JsonObject effectlist = effectjsonobj.get("effects").getAsJsonObject();
				for(Map.Entry<String, JsonElement> entry : effectlist.entrySet())
					effect.addEffect(new ResourceLocation(entry.getKey()), entry.getValue().getAsFloat());
				
				SurvivalInc.logger.info("Adding ISE: " + effect.toString());
				effects.add(effect);
			}
			catch(IllegalArgumentException exc)
			{
				SurvivalInc.logger.warn(exc.getMessage());
			}
			catch(JsonParseException exc)
			{
				SurvivalInc.logger.error("Invalid element specification: " + effectjson.toString(), exc);
			}
		}
		
		return effects;
	}
	
	@FunctionalInterface
	public static interface SituationEffectFactory
	{
		public ItemSituation<?> createEffect(Item item, Properties props);
	}
	
}
