package enginecrafter77.survivalinc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.util.ResourceLocation;

/**
 * ExternalResourceProvider is a class that facilitates loading of files that are meant to be modified by the user.
 * Usually it's used for configuration files. These configuration files have their default versions stored inside the
 * JAR. On new installations, the file is copied from the JAR to it's final destination, where it can be adjusted by the
 * user to suit their needs.
 * @author Enginecrafter77
 */
public class ExternalResourceProvider {
	/** The {@link ResourceLocation} of the default file inside the JAR */
	public final ResourceLocation defaults;
	
	/** The final location of the file */
	private final File targetfile;
	
	public ExternalResourceProvider(File file, ResourceLocation defaults)
	{
		this.defaults = defaults;
		this.targetfile = file;
	}
	
	/**
	 * @return The file instance representing the final path of the resource
	 */
	public File getFile()
	{
		return this.targetfile;
	}
	
	/**
	 * Loads the data from the file using the provided {@link ResourceLoader}. If the file doesn't exist in it's expected
	 * location (e.g. a new install), it's copied there from the JAR.
	 * @param loader The {@link ResourceLoader} instance which should act upon the read data.
	 */
	public void load(ResourceLoader loader)
	{
		try
		{
			if(!this.targetfile.exists())
			{
				try
				{
					File dir = this.targetfile.getParentFile();
					if(!dir.exists()) dir.mkdir();
					
					Files.copy(SurvivalInc.class.getResourceAsStream(String.format("/assets/%s/%s", this.defaults.getNamespace(), this.defaults.getPath())), this.targetfile.toPath());
				}
				catch(FileAlreadyExistsException exc)
				{
					// Do nothing
				}
			}
			
			FileInputStream input = new FileInputStream(this.targetfile);
			loader.load(input);
			input.close();
		}
		catch(IOException exc)
		{
			throw new RuntimeException("Failed to load " + this.targetfile.getAbsolutePath(), exc);
		}
	}
	
	/**
	 * ResourceLoader is an interface specifying a consumer of stream read data.
	 * @author Enginecrafter77
	 */
	@FunctionalInterface
	public static interface ResourceLoader {
		public void load(InputStream input) throws IOException;
	}
}
