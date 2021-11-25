package enginecrafter77.survivalinc.stats.impl.armor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.EnumMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import net.minecraft.item.ItemArmor;
import net.minecraft.util.IJsonSerializable;

/**
 * ConfigurableArmorModifier is an extension of {@link enginecrafter77.survivalinc.stats.impl.armor.ArmorModifier
 * ArmorModifer} designed to be dynamically altered during runtime. ConfigurableArmorModifer also provides methods for
 * convenient serialization to JSON objects by implementing {@link IJsonSerializable}.
 * @see enginecrafter77.survivalinc.stats.impl.armor.ArmorModifier ArmorModifier
 * @author Enginecrafter77
 */
public class ConfigurableArmorModifier extends ArmorModifier implements IJsonSerializable {
	/** The defaults for {@link MutableDistributionVector} initialization */
	public static final Map<ArmorPiece, Float> DEFAULT_VECTOR = ImmutableMap.of(ArmorPiece.HELMET, 0.2F, ArmorPiece.CHESTPLATE, 0.35F, ArmorPiece.LEGGINGS, 0.3F, ArmorPiece.BOOTS, 0.15F);
	
	/**
	 * Creates a new ConfigurableArmorModifier using the default {@link MutableDistributionVector} constructor.
	 * @see MutableDistributionVector#MutableDistributionVector()
	 */
	public ConfigurableArmorModifier()
	{
		super(new MutableDistributionVector());
	}
	
	@Override
	public void setDistributionVector(ConductivityDistributionVector cdcvector)
	{
		this.getDistributionVector().copyFrom(cdcvector);
	}
	
	@Override
	public MutableDistributionVector getDistributionVector()
	{
		return (MutableDistributionVector)super.getDistributionVector();
	}
	
	/**
	 * Attempts to load JSON data from the provided stream. After a {@link JsonElement} instance is serialized, it's
	 * forwarded to {@link #fromJson(JsonElement)}.
	 * @param from The input stream to load the JSON from
	 * @throws IOException When an error occurs during read from the stream
	 */
	public void load(InputStream from) throws IOException
	{
		InputStreamReader reader = new InputStreamReader(from);
		JsonParser parser = new JsonParser();
		JsonObject root = parser.parse(reader).getAsJsonObject();
		this.fromJson(root);
		reader.close();
	}
	
	/**
	 * Attempts to serialize the object to JSON, which is subsequently written to the specified {@link OutputStream}.
	 * @param to The output stream to write the JSON to
	 * @throws IOException WHen an error occurs during write to the stream
	 */
	public void save(OutputStream to) throws IOException
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonWriter jwriter = new JsonWriter(new OutputStreamWriter(to));
		jwriter.setIndent("\t");
		gson.toJson(this.getSerializableElement(), jwriter);
		jwriter.close();
	}
	
	@Override
	public void fromJson(JsonElement element)
	{
		JsonObject root = element.getAsJsonObject();
		
		JsonObject distribution = root.getAsJsonObject("distribution");
		
		MutableDistributionVector cdcv = this.getDistributionVector();
		for(Map.Entry<String, JsonElement> entry : distribution.entrySet())
		{
			ArmorPiece piece = ArmorPiece.valueOf(entry.getKey().toUpperCase());
			float value = entry.getValue().getAsFloat();
			cdcv.setValueFor(piece, value);
		}
		
		JsonObject materials = root.getAsJsonObject("materials");
		for(Map.Entry<String, JsonElement> entry : materials.entrySet())
		{
			ItemArmor.ArmorMaterial material = ItemArmor.ArmorMaterial.valueOf(entry.getKey());
			float value = entry.getValue().getAsFloat();
			this.setMaterialConductivity(material, value);
		}
	}
	
	@Override
	public JsonElement getSerializableElement()
	{
		JsonObject dstvec = new JsonObject();
		for(Map.Entry<ArmorPiece, Float> entry : this.cdcvector)
			dstvec.add(entry.getKey().name().toLowerCase(), new JsonPrimitive(entry.getValue()));
		
		JsonObject materials = new JsonObject();
		for(Map.Entry<ItemArmor.ArmorMaterial, Float> entry : this.materialmap.entrySet())
			materials.add(entry.getKey().name(), new JsonPrimitive(entry.getValue()));
		
		JsonObject root = new JsonObject();
		root.add("distribution", dstvec);
		root.add("materials", materials);
		return root;
	}
	
	/**
	 * MutableDistributionVector is a {@link ArmorModifier.ConductivityDistributionVector} extension which allows for
	 * changing of the vector's components without the need to copy it.
	 * @author Enginecrafter77
	 */
	public static class MutableDistributionVector extends ArmorModifier.ConductivityDistributionVector {
		/**
		 * Creates {@link MutableDistributionVector} using the {@link ConfigurableArmorModifier#DEFAULT_VECTOR default values}
		 */
		public MutableDistributionVector()
		{
			super(new EnumMap<ArmorPiece, Float>(ConfigurableArmorModifier.DEFAULT_VECTOR));
		}
		
		/**
		 * Sets the value for the vector component associated with the specified {@link ArmorPiece}.
		 * @param piece The armor piece associated with the vector component
		 * @param value The value to be assigned to the component
		 */
		public void setValueFor(ArmorPiece piece, float value)
		{
			this.vector.put(piece, value);
			ConductivityDistributionVector.normalize(this.vector, this.vector::put);
		}
		
		/**
		 * Copies the data from the provided source vector to this vector.
		 * @param source The source vector to copy data from
		 */
		public void copyFrom(ArmorModifier.ConductivityDistributionVector source)
		{
			this.vector.putAll(source.vector);
		}
	}
}
