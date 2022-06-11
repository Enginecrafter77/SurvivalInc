package enginecrafter77.survivalinc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.util.ResourceLocation;

/**
 * ExportedResource is a class that facilitates loading of files that are meant to be modified by the user.
 * Usually it's used for configuration files. These configuration files have their default versions stored inside the
 * JAR. On new installations, the file is copied from the JAR to its final destination, where it can be adjusted by the
 * user to suit their needs.
 * @author Enginecrafter77
 */
public class ExportedResource {
	/** The {@link ResourceLocation} of the default file inside the JAR */
	public final ResourceLocation defaultLocation;
	
	/** The final location of the file */
	private final File exportedFile;

	private final URL defaultUrl;

	public ExportedResource(File exportTo, ResourceLocation defaultLocation, ResourceAssetPathResolver resolver)
	{
		this.defaultLocation = defaultLocation;
		this.exportedFile = exportTo;

		String assetPath = resolver.resolveAssetPath(defaultLocation);
		this.defaultUrl = SurvivalInc.class.getResource(assetPath);

		if(this.defaultUrl == null)
			throw new IllegalStateException(new NoSuchFileException("Resource " + assetPath + " not found!"));
	}

	public ExportedResource(File exportTo, ResourceLocation defaultLocation)
	{
		this(exportTo, defaultLocation, ResourceAssetPathResolver.DEFAULT);
	}
	
	/**
	 * @return The file instance representing the final path of the resource
	 */
	public File getFile()
	{
		return this.exportedFile;
	}

	public URL getDefaultResourceUrl()
	{
		return this.defaultUrl;
	}

	/**
	 * Loads the data from the file using the provided {@link ResourceConsumer}. If the file doesn't exist in it's expected
	 * location (e.g. a new install), it's copied there from the JAR.
	 * @param consumer The {@link ResourceConsumer} instance which should act upon the read data.
	 */
	public void load(ResourceConsumer consumer)
	{
		try
		{
			if(!this.exportedFile.exists())
			{
				try
				{
					File dir = this.exportedFile.getParentFile();
					if(!dir.exists()) dir.mkdir();

					InputStream input = this.defaultUrl.openStream();
					Files.copy(input, this.exportedFile.toPath());
					input.close();
				}
				catch(FileAlreadyExistsException exc)
				{
					// Impossible
				}
			}
			
			FileInputStream input = new FileInputStream(this.exportedFile);
			consumer.load(input);
			input.close();
		}
		catch(IOException exc)
		{
			throw new RuntimeException("Failed to load " + this.exportedFile.getAbsolutePath(), exc);
		}
	}

	@FunctionalInterface
	public static interface ResourceAssetPathResolver
	{
		public static final ResourceAssetPathResolver DEFAULT = (ResourceLocation source) -> String.format("/assets/%s/%s", source.getNamespace(), source.getPath());

		public String resolveAssetPath(ResourceLocation source);
	}

	/**
	 * ResourceConsumer is an interface specifying a consumer of stream read data.
	 * @author Enginecrafter77
	 */
	@FunctionalInterface
	public static interface ResourceConsumer
	{
		public void load(InputStream input) throws IOException;
	}
}
