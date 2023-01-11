package enginecrafter77.survivalinc.season.melting;

import com.google.common.collect.ImmutableList;
import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public class MelterSetupEvent extends Event {
	private final Set<MelterEntry> entries;

	private MeltingFilterCompiler compiler;

	public MelterSetupEvent()
	{
		this.compiler = MeltingBehavior.NONE;
		this.entries = new LinkedHashSet<MelterEntry>();
	}

	public void setCompiler(MeltingFilterCompiler compiler)
	{
		this.compiler = compiler;
	}

	public AttachedMelterEntryBuilder beginEntry(BlockMelting block)
	{
		return new AttachedMelterEntryBuilder(block);
	}

	public void addMelterEntry(MelterEntry entry)
	{
		this.entries.add(entry);
	}

	public <CTRL extends MeltingController> CTRL buildController(Supplier<CTRL> controllerFactory)
	{
		CTRL controller = controllerFactory.get();
		this.configureController(controller);
		return controller;
	}

	public void configureController(MeltingController controller)
	{
		controller.clearMeltingMap();
		controller.registerMelterEntries(this.entries);
		controller.setCompiler(this.compiler);
	}

	@Override
	public boolean isCancelable()
	{
		return false;
	}

	@Override
	public boolean hasResult()
	{
		return false;
	}

	public class AttachedMelterEntryBuilder
	{
		private final BlockMelting block;
		private final ImmutableList.Builder<BlockPositionLevelMapper> levelmap;

		public AttachedMelterEntryBuilder(BlockMelting block)
		{
			this.levelmap = ImmutableList.builder();
			this.block = block;
		}

		public AttachedMelterEntryBuilder onSurface(int offset)
		{
			this.levelmap.add(new SurfaceLevelMapper(offset));
			return this;
		}

		public AttachedMelterEntryBuilder onLevel(int level)
		{
			this.levelmap.add(new AbsoluteLevelMapper(level));
			return this;
		}

		public AttachedMelterEntryBuilder on(BlockPositionLevelMapper mapper)
		{
			this.levelmap.add(mapper);
			return this;
		}

		public void register()
		{
			MelterSetupEvent.this.addMelterEntry(new MelterEntry(this.block, this.levelmap.build()));
		}
	}
}
