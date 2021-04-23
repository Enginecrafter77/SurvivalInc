package enginecrafter77.survivalinc.stats.effect.item;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemSituationMapper {
	
	public final List<ItemSituationEffect> effects;
	
	public final Map<String, Function<String[], EffectiveSituation<?>>> situations;
	
	public ItemSituationMapper()
	{
		this.effects = new LinkedList<ItemSituationEffect>();
		this.situations = new HashMap<String, Function<String[], EffectiveSituation<?>>>();
	}
	
	@SubscribeEvent
	public void invoke(Event event)
	{
		for(ItemSituationEffect effect : ItemSituationMapper.this.effects)
			effect.apply(event);
	}
	
	public void addSituationFactory(String name, Function<String[], EffectiveSituation<?>> situation)
	{
		this.situations.put(name, situation);
	}
	
	public void load(InputStream source) throws IOException
	{
		JsonParser parser = new JsonParser();
		JsonArray index = parser.parse(new InputStreamReader(source)).getAsJsonArray();
		
		for(JsonElement effectjson : index)
		{
			JsonObject effectjsonobj = effectjson.getAsJsonObject();
			
			String factoryname = effectjsonobj.get("type").getAsString();
			Function<String[], EffectiveSituation<?>> factory = this.situations.get(factoryname);
			if(factory == null)
			{
				SurvivalInc.logger.error("Situation \"{}\" not registered.", factoryname);
				continue;
			}
			
			String[] parameters;
			JsonElement parametersjson = effectjsonobj.get("parameters");
			if(parametersjson != null)
			{
				int pindex = 0;
				JsonArray array = effectjsonobj.get("parameters").getAsJsonArray();
				parameters = new String[array.size()];
				for(JsonElement element : array)
					parameters[pindex++] = element.getAsString();
			}
			else parameters = new String[0];
			
			EffectiveSituation<?> situation = factory.apply(parameters);
			ItemSituationEffect effect = new ItemSituationEffect(situation);
			
			JsonObject effectlist = effectjsonobj.get("effects").getAsJsonObject();
			for(Map.Entry<String, JsonElement> entry : effectlist.entrySet())
				effect.addEffect(new ResourceLocation(entry.getKey()), entry.getValue().getAsFloat());
			
			this.effects.add(effect);
		}
	}
	
}
