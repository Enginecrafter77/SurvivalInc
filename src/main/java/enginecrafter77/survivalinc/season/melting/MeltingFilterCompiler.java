package enginecrafter77.survivalinc.season.melting;

import javax.annotation.Nullable;

public interface MeltingFilterCompiler {
	@Nullable
	public ChunkFilter compile(MelterEntry entry);
}
