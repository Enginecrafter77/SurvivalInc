package enginecrafter77.survivalinc.util.blockprop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.block.Block;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class BlockPropertyJsonParser {
	private final BlockPropertiesBuilder<?> builder;

	public BlockPropertyJsonParser(BlockPropertiesBuilder<?> builder)
	{
		this.builder = builder;
	}
	public BlockProperties fromJson(JsonElement json)
	{
		JsonObject rootMap = json.getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry : rootMap.entrySet())
		{
			Block block = Block.getBlockFromName(entry.getKey());
			if(block == null)
				continue;
			JsonElement valueElement = entry.getValue();
			if(valueElement.isJsonObject())
			{
				JsonObject valueObject = valueElement.getAsJsonObject();
				for(Map.Entry<String, JsonElement> valueEntry : valueObject.entrySet())
				{
					Object ret = this.convertElementToObject(valueEntry.getValue());
					if(ret == null)
						continue;
					this.builder.put(block, valueEntry.getKey(), ret);
				}
			}
			else if(valueElement.isJsonPrimitive())
			{
				Object ret = this.convertElementToObject(valueElement);
				this.builder.put(block, BlockPropertyMap.KEY_SINGULAR_PROPERTY, Objects.requireNonNull(ret));
			}
		}

		return this.builder.build();
	}

	@Nullable
	protected Object convertElementToObject(JsonElement content)
	{
		if(!content.isJsonPrimitive())
			return null;
		JsonPrimitive primitive = content.getAsJsonPrimitive();
		if(primitive.isNumber())
		{
			return primitive.getAsFloat();
		}
		else if(primitive.isBoolean())
		{
			return primitive.getAsBoolean();
		}
		else
		{
			return primitive.getAsString();
		}
	}
}
