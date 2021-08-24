package enginecrafter77.survivalinc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;

/**
 * ForgeASMInjector is a reflection helper that allows
 * direct interaction with {@link ASMEventHandler#LOADER}.
 * ASMEventHandler's LOADER is a class loader used to
 * dynamically generate {@link net.minecraftforge.fml.common.eventhandler.IEventListener IEventListener}
 * instances from methods annotated by {@link net.minecraftforge.fml.common.eventhandler.SubscribeEvent SubscribeEvent}.
 * The generated IEventListener classes are defined inside
 * the ClassLoader referenced by {@link ForgeASMInjector#asmclassloader}.
 * Furthermore, this class allows creating classes from bytecode
 * inside the ASMEventHandler's LOADER, which makes them visible
 * from within it's class loading context.
 * @author Enginecrafter77
 */
public class ForgeASMInjector {
	/** The lazily initialized ForgeASMInjector singleton instance */
	private static ForgeASMInjector instance;
	
	/** The ClassLoader used by {@link ASMEventHandler} */
	public final ClassLoader asmclassloader;
	
	/** The ASMEventHandler.ASMClassLoader class */
	private final Class<?> asmloaderclass;
	
	/** The ASMEventHandler.LOADER field */
	private final Field asmloaderfield;
	
	/** The ASMEventHandler.ASMClassLoader.define() method */
	private final Method injectmethod;
	
	protected ForgeASMInjector() throws ReflectiveOperationException
	{
		this.asmloaderclass = ASMEventHandler.class.getDeclaredClasses()[0];
		this.asmloaderfield = ASMEventHandler.class.getDeclaredField("LOADER");
		this.injectmethod = asmloaderclass.getDeclaredMethod("define", new Class<?>[]{String.class, byte[].class});
		
		this.asmloaderfield.setAccessible(true);
		this.injectmethod.setAccessible(true);
		
		this.asmclassloader = (ClassLoader)this.asmloaderfield.get(null);
	}
	
	/**
	 * Returns the singleton instance of ForgeASMInjector.
	 * If the method is run first time, a new instance is
	 * created.
	 * @return A singleton instance of ForgeASMInjector
	 */
	public static ForgeASMInjector getInstance()
	{
		if(ForgeASMInjector.instance == null)
		{
			try
			{
				ForgeASMInjector.instance = new ForgeASMInjector();
			}
			catch(ReflectiveOperationException exc)
			{
				SurvivalInc.logger.error("Failed to create ForgeASMInjector", exc);
			}
		}
		return ForgeASMInjector.instance;
	}
	
	/**
	 * Injects a new class to the forge's ASM class loader. This class
	 * will be visible from the {@link ASMEventHandler}'s context, making
	 * it viable for registering on-demand generated event handler classes.
	 * @param name The name of the class or null if the name is unknown (or irrelevant)
	 * @param data The bytecode of the class
	 * @return A newly created class instance
	 */
	public Class<?> injectClass(@Nullable String name, byte[] data)
	{
		try
		{
			return (Class<?>)injectmethod.invoke(this.asmclassloader, name, data);
		}
		catch(ReflectiveOperationException exc)
		{
			throw new RuntimeException("Injecting class to ASMClassLoader failed.", exc);
		}
	}
}