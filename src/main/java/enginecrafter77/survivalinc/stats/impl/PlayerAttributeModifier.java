package enginecrafter77.survivalinc.stats.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.modifier.Modifier;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerAttributeModifier extends AttributeModifier implements Modifier<EntityPlayer>
{
	private static final float error = 0.25F; // The correction to avoid totally nullification of the speed
	
	private final Set<EntityPlayer> registered;
	
	public final StatProvider provider;
	public final IAttribute attribute;
	
	protected float scale;
	
	private float value;
	
	private Method update_method;
	
	public PlayerAttributeModifier(StatProvider provider, IAttribute attribute, float scale)
	{
		super("wetness_slowdown", 0, 1);
		this.registered = new HashSet<EntityPlayer>();
		this.attribute = attribute;
		this.provider = provider;
		this.scale = scale;
		
		String[] possible_names = {"func_111131_f", "flagForUpdate"}; // Thanks MCP team :)
		this.update_method = null;
		for(String name : possible_names)
		{
			try
			{
				this.update_method = ModifiableAttributeInstance.class.getDeclaredMethod(name, new Class<?>[0]);
				this.update_method.setAccessible(true);
			}
			catch(ReflectiveOperationException exc)
			{
				SurvivalInc.logger.info("Method {} not found in ModifiableAttributeInstance");
			}
		}
		
		if(this.update_method == null)
			throw new UnsupportedOperationException("Update method not found in ModifiableAttributeInstance. Perhaps wrong MCP mappings?");
	}
	
	public PlayerAttributeModifier(StatProvider provider, IAttribute attribute)
	{
		this(provider, attribute, 1F);
	}
	
	// TODO implement using AccessTransformer
	public void enforceUpdate(IAttributeInstance instance)
	{
		try
		{
			//TODO replace invoking this method with ModifiableAttributeInstance wrapper object (just implements IAttributeInstance)
			this.update_method.invoke(instance, new Object[0]);
		}
		catch(ReflectiveOperationException exc)
		{
			SurvivalInc.logger.error("Unable to force update of ModifiableAttributeInstance: " + exc.getMessage());
			exc.printStackTrace();
		}
	}
	
	@Override
	public boolean shouldTrigger(EntityPlayer target, float level)
	{
		return true;
	}
	
	@Override
	public float apply(EntityPlayer target, float current)
	{
		this.value = current;
		
		IAttributeInstance inst = target.getAttributeMap().getAttributeInstance(attribute);
		if(!this.registered.contains(target))
		{
			inst.applyModifier(this);
			this.registered.add(target);
		}
		this.enforceUpdate(inst);
		
		return current;
	}

	int tick = 0;
	
	@Override
	public double getAmount()
	{
		float max = provider.getMaximum();
		float threshold = this.getThreshold();
		
		if(value > (threshold * max))
		{
			/*
			 * We want to achieve following scenario:
			 * 	When the wetness is just at the slowdown threshold, apply zero slowdown (closest 0)
			 * 	As the wetness is rising, slow the player down in linear manner (which implies using direct relationship function)
			 * 	When the wetness is at maximum, the value should be just above -1 (to avoid total nullification of the speed)
			 * After some experimentation and calculations, I came up with this little equation:
			 * 	      1 - g           g - 1
			 * 	y = ---------- x + t -------
			 * 	     m(t - 1)         t - 1
			 * It may seem a little bit complicated, but you would get to that anyways if you would try to solve it.
			 */
			float inclination = (1 - error) / (max * (threshold - 1)); // The inclination of the graph, aka the A
			float offset = threshold * ((error - 1) / (threshold - 1)); // The offset of the graph, aka the B
			float fraction = inclination * value + offset; // Direct relationship formula
			
			return scale * fraction;
		}
		else return 0F;
	}
	
	@Override
	public boolean isSaved()
	{
		return false;
	}
	
	public float getThreshold()
	{
		return (float)ModConfig.MECHANICS.wetnessSlowdownThreshold / 100F;
	}
}