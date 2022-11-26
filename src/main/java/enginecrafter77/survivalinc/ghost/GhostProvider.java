package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.HUDConstructEvent;
import enginecrafter77.survivalinc.client.StackingElementLayoutFunction;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.stats.*;
import enginecrafter77.survivalinc.stats.effect.*;
import enginecrafter77.survivalinc.util.VectorOriginDistanceComparator;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

public class GhostProvider implements StatProvider<GhostEnergyRecord> {
	public static final EffectFilter<GhostEnergyRecord> FILTER_ACTIVE = (GhostEnergyRecord record, EntityPlayer player) -> record.isActive();
	public static final HelicalParticleSpawner HELICAL_PARTICLE_SPAWNER = new HelicalParticleSpawner(EnumParticleTypes.PORTAL).setHelixCount(8);
	public static final Vec3d rp_box = new Vec3d(0.6D, 1.5D, 0.6D), rp_offset = new Vec3d(0D, -0.3D, 0D);
	
	public final EffectApplicator<GhostEnergyRecord> applicator;
	public final InteractionProcessor interactor;
	
	public GhostProvider()
	{
		this.applicator = new EffectApplicator<GhostEnergyRecord>();
		this.interactor = new InteractionProcessor(PlayerInteractEvent.RightClickBlock.class, (float)ModConfig.GHOST.interactionCost);
		
		EffectFilter<StatRecord> playerSprinting = FunctionalEffectFilter.byPlayer(EntityPlayer::isSprinting);
		this.applicator.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, (float)ModConfig.GHOST.passiveNightRegen)).addFilter(this::isNightGainApplicable);
		this.applicator.add(this::onGhostUpdate);
		
		this.applicator.add(this::resurrectTick);
		if(ModConfig.GHOST.allowFlying) this.applicator.add(this::tickGhostFlight);
		if(ModConfig.GHOST.sprintingEnergyDrain > 0)
		{
			this.applicator.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -(float)ModConfig.GHOST.sprintingEnergyDrain)).addFilter(playerSprinting);
			this.applicator.add(this::spawnSprintingParticles).addFilter(SideEffectFilter.CLIENT).addFilter(GhostProvider.FILTER_ACTIVE).addFilter(playerSprinting);
			this.applicator.add(this::synchronizeFood).addFilter(GhostProvider.FILTER_ACTIVE);
		}
		
		this.interactor.addBlockClass(BlockDoor.class, 1F);
		this.interactor.addBlockClass(BlockFenceGate.class, 0.9F);
		this.interactor.addBlockClass(BlockRedstoneComparator.class, 2F);
		this.interactor.addBlockClass(BlockRedstoneRepeater.class, 2F);
		this.interactor.addBlockClass(BlockTrapDoor.class, 0.9F);
		this.interactor.setBlockCost(Blocks.STONE_BUTTON, 0.6F);
		this.interactor.setBlockCost(Blocks.WOODEN_BUTTON, 0.5F);
		this.interactor.setBlockCost(Blocks.LEVER, 0.75F);
	}
	
	@Override
	public void update(EntityPlayer target, GhostEnergyRecord ghost)
	{		
		if(ghost.shouldReceiveTicks())
		{
			this.applicator.apply(ghost, target);
			ghost.checkoutValueChange();
		}
	}
	
	@Override
	public ResourceLocation getStatID()
	{
		return new ResourceLocation(SurvivalInc.MOD_ID, "ghostenergy");
	}

	@Override
	public GhostEnergyRecord createNewRecord()
	{
		return new GhostEnergyRecord();
	}
	
	@Override
	public Class<GhostEnergyRecord> getRecordClass()
	{
		return GhostEnergyRecord.class;
	}
	
	public int energyToFood(GhostEnergyRecord record)
	{
		return Math.round(4F + 16F * record.getNormalizedValue());
	}
	
	//==================================
	//=========[Event Handling]=========
	//==================================
	
	@SubscribeEvent
	public void registerStat(StatRegisterEvent event)
	{
		event.register(this);
	}

	@SubscribeEvent
	public void constructHud(HUDConstructEvent event)
	{
		event.addElement(new GhostEnergyBar(), StackingElementLayoutFunction.LEFT).setTrigger(RenderGameOverlayEvent.ElementType.HOTBAR).addFilter(GhostConditionRenderFilter.INSTANCE);
		event.addRenderStageFilter(GhostConditionRenderFilter.INSTANCE, RenderGameOverlayEvent.ElementType.HEALTH, RenderGameOverlayEvent.ElementType.AIR, RenderGameOverlayEvent.ElementType.ARMOR, RenderGameOverlayEvent.ElementType.FOOD);
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		EntityPlayer player = event.player;
		if(!event.isEndConquered())
		{
			StatCapability.obtainRecord(this, player).ifPresent((GhostEnergyRecord record) -> {
				record.setActive(true);
				StatCapability.synchronizeStats(StatSyncMessage.withPlayer(player));
			});
		}
	}
	
	/**
	 * Takes care of placing interaction toll on ghosts.
	 * @param event The interaction event
	 */
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		StatCapability.obtainRecord(this, event.getEntity()).ifPresent((GhostEnergyRecord record) -> {
			if(record.isActive())
			{
				float energy = record.getValue();
				if(ModConfig.GHOST.enableInteraction && energy >= ModConfig.GHOST.interactionThreshold)
				{
					Float cost = this.interactor.apply(event);
					if(cost != null && energy >= cost)
					{
						record.addToValue(-cost);
						GhostProvider.spawnInteractionParticles(event.getEntityPlayer(), event.getPos(), cost);
						return;
					}
				}

				if(event.isCancelable()) event.setCanceled(true);
				if(event.hasResult()) event.setResult(Result.DENY);
			}
		});
	}
	
	/**
	 * Makes sure ghosts won't be able to hit mobs. If they attempt to do so,
	 * a tiny bit of their ghost energy will jump to the victim, thus draining
	 * the ghost's energy. 
	 * @param event The attack event
	 */
	@SubscribeEvent
	public void onPlayerHitEntity(LivingAttackEvent event)
	{
		DamageSource source = event.getSource();
		if(source instanceof EntityDamageSource)
		{
			EntityDamageSource attack = (EntityDamageSource)source;
			Entity attacker = attack.getTrueSource();
			if(attacker instanceof EntityPlayer)
			{
				StatCapability.obtainRecord(this, event.getEntity()).ifPresent((GhostEnergyRecord record) -> {
					if(record.isActive())
					{
						if(record.isActive())
						{
							Entity victim = event.getEntity();
							Random rng = victim.world.rand;
							for(int pass = 32; pass > 0; pass--)
							{
								victim.world.spawnParticle(EnumParticleTypes.CLOUD, victim.posX + victim.width * (rng.nextDouble() - 0.5), victim.posY + victim.height * rng.nextGaussian(), victim.posZ + victim.width * (rng.nextDouble() - 0.5), 0, 0, 0);
							}
							record.addToValue(-1F); // We need to punish the player a lil' bit
							event.setCanceled(true);
						}
					}
				});
			}
		}
	}
	
	/**
	 * Called when an entity dies, so nearby ghosts
	 * can drain its life energy and use it to reform
	 * their former bodies.
	 * @param event The entity death event
	 */
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		EntityLivingBase target = event.getEntityLiving();
		Vec3d origin = target.getPositionVector().add(0D, target.height / 2D, 0D);
		AxisAlignedBB box = GhostProvider.boxAroundPoint(origin, new Vec3d(6D, 2D, 6D));
		
		target.world.getEntitiesWithinAABB(EntityPlayer.class, box).stream()
				.sorted(Comparator.comparing(Entity::getPositionVector, new VectorOriginDistanceComparator(origin)))
				.map((EntityPlayer player) -> StatCapability.obtainRecord(this, player).orElse(null))
				.filter(Objects::nonNull)
				.filter(GhostEnergyRecord::isActive)
				.findFirst()
				.ifPresent(GhostEnergyRecord::tickResurrection);
	}
	
	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event)
	{
		if(StatCapability.obtainRecord(this, event.getEntity()).map(GhostEnergyRecord::isActive).orElse(false))
			event.setCanceled(true);
	}
	
	/**
	 * Makes sure that ghosts are not attacked by any mobs whatsoever.
	 * @param event The visibility modifier event
	 */ 
	@SubscribeEvent
	public void modifyVisibility(PlayerEvent.Visibility event)
	{
		if(StatCapability.obtainRecord(this, event.getEntity()).map(GhostEnergyRecord::isActive).orElse(false))
			event.modifyVisibility(0D);
	}
	
	/**
	 * Makes sure that ghosts won't be able to move while being resurrected
	 * @param event The movement input update event
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void blockMovementWhileResurrecting(InputUpdateEvent event)
	{
		if(ModConfig.GHOST.resurrectionBlocksMovement && StatCapability.obtainRecord(this, event.getEntity()).map(GhostEnergyRecord::isResurrectionActive).orElse(false))
		{
			MovementInput input = event.getMovementInput();
			input.forwardKeyDown = false;
			input.rightKeyDown = false;
			input.backKeyDown = false;
			input.leftKeyDown = false;
			input.moveForward = 0F;
			input.moveStrafe = 0F;
			input.sneak = false;
			input.jump = false;
		}
	}
	
	//==================================
	//=======[Functional Effects]=======
	//==================================
	
	/**
	 * Takes care of setting ghosts invulnerable,
	 * disabling bobbing and suspending all the
	 * other stats.
	 * @param record The ghost energy record
	 * @param player The player to apply the changes to
	 */
	public void onGhostUpdate(GhostEnergyRecord record, EntityPlayer player)
	{		
		if(record.hasPendingChange())
		{
			boolean isGhost = record.isActive();
			player.capabilities.disableDamage = isGhost;
			player.capabilities.allowEdit = !isGhost;
			
			// A dirty hack to disable bobbing. Probably not the best way to do it, but whatever.
			if(player.world.isRemote && player == Minecraft.getMinecraft().player)
				Minecraft.getMinecraft().gameSettings.viewBobbing = !isGhost;
			
			// Suspend all other stats
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			if(tracker != null)
			{
				Collection<StatProvider<?>> providers = tracker.getRegisteredProviders();
				providers.remove(this);
				for(StatProvider<?> provider : providers)
					tracker.setSuspended(provider, isGhost);
			}
			
			record.acceptChange();
		}
	}
	
	/**
	 * Synchronizes player's food level with the ghost energy.
	 * This makes sure that players with little ghost energy
	 * would not be able to sprint.
	 * @param record The ghost energy record
	 * @param player The player to apply the effect to
	 */
	public void synchronizeFood(GhostEnergyRecord record, EntityPlayer player)
	{
		FoodStats food = player.getFoodStats();
		food.setFoodLevel(this.energyToFood(record));
	}
	
	/**
	 * Used to process ghost resurrection ticks.
	 * @param record The ghost energy record
	 * @param player The player to apply the effect to
	 */
	public void resurrectTick(GhostEnergyRecord record, EntityPlayer player)
	{
		if(record.isResurrectionActive())
		{
			record.tickResurrection();
			
			Vec3d origin = player.getPositionVector().add(0D, player.height / 2D, 0D);
			if(player.world.isRemote)
			{
				WorldClient world = (WorldClient)player.world;
				if(record.timeUntilResurrection() == 60)
				{
					world.playSound(player, origin.x, origin.y, origin.z, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.8F, 1F);
				}
				GhostProvider.HELICAL_PARTICLE_SPAWNER.spawn(world, origin.add(rp_offset), rp_box, Vec3d.ZERO, player.ticksExisted);
			}
			
			if(record.isResurrectionReady())
			{
				record.finishResurrection();
				
				if(!player.world.isRemote)
				{
					// Server code
					WorldServer world = (WorldServer)player.world;
					world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, origin.x, origin.y, origin.z, 10, 0D, 0D, 0D, 0D);
					world.playSound(null, origin.x, origin.y, origin.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.6F, 1F);
				}
			}
		}
	}
	
	/**
	 * Spawns cloud particles representing emitted ghost
	 * energy while the ghost is "sprinting".
	 * @param record The ghost energy record
	 * @param player The player to apply the effect to
	 */
	public void spawnSprintingParticles(GhostEnergyRecord record, EntityPlayer player)
	{
		WorldClient world = (WorldClient)player.world;
		world.spawnParticle(EnumParticleTypes.CLOUD, player.lastTickPosX, player.lastTickPosY + (player.height / 2), player.lastTickPosZ, -player.motionX, 0.1D, -player.motionZ);
	}
	
	/**
	 * Makes sure that ghosts with enough energy are able to fly
	 * as long as their energy is above the flying threshold.
	 * @param record The ghost energy record
	 * @param player The player to apply the effect to
	 */
	public void tickGhostFlight(GhostEnergyRecord record, EntityPlayer player)
	{
		boolean shouldFly = record.isActive() && record.getValue() > ModConfig.GHOST.flyingThreshold;
		if(player.capabilities.allowFlying != shouldFly) player.capabilities.allowFlying = shouldFly;
		
		if(player.capabilities.isFlying)
		{
			record.addToValue(-(float)ModConfig.GHOST.flyingDrain);
			if(record.getValue() < ModConfig.GHOST.flyingThreshold) player.capabilities.isFlying = false;
		}
	}

	public boolean isNightGainApplicable(GhostEnergyRecord record, EntityPlayer player)
	{
		boolean night;
		if(player.world.isRemote)
		{
			float angle = player.world.getCelestialAngle(1F);
			night = angle < 0.75F && angle > 0.25F;
		}
		else night = !player.world.isDaytime();
		return night;
	}
	
	//=====================================
	//=======[Miscellaneous Methods]=======
	//=====================================
	
	/**
	 * A simple method to return a box with the specified sizes
	 * centered at the specified point. This method is used mainly
	 * to circumvent the stupid forge's SideOnly restriction on
	 * {@link AxisAlignedBB#AxisAlignedBB(Vec3d, Vec3d) AABB's Vec3D constructor}.
	 * @param point The center of the box
	 * @param size The size of the box
	 * @return AxisAlignedBox with the specified size centered around the specified point
	 */
	public static AxisAlignedBB boxAroundPoint(Vec3d point, Vec3d size)
	{
		Vec3d offset = size.scale(0.5D);
		Vec3d start = point.subtract(offset);
		Vec3d end = point.add(offset);
		return new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z);
	}
	
	/**
	 * Spawns a cloud around the block the player has right-clicked.
	 * @param player The player to apply the effect to
	 * @param position The position to spawn the particles at
	 * @param cost The cost of spawning the particles
	 */
	public static void spawnInteractionParticles(EntityPlayer player, BlockPos position, float cost)
	{
		if(player.world.isRemote)
		{
			AxisAlignedBB box = player.world.getBlockState(position).getBoundingBox(player.world, position).grow(0.1D);
			Vec3d offbound = new Vec3d(box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minY);
			Vec3d center = new Vec3d(position).add(box.getCenter());
			
			Random rng = player.world.rand;
			for(int pass = Math.round(cost * 2F); pass > 0; pass--)
			{
				Vec3d randoff = new Vec3d(rng.nextDouble() - 0.5D, rng.nextDouble() - 0.5D, rng.nextDouble() - 0.5D);
				Vec3d pos = center.add(offbound.x * randoff.x, offbound.y * randoff.y, offbound.z * randoff.z);
				player.world.spawnParticle(EnumParticleTypes.CLOUD, pos.x, pos.y, pos.z, 0, 0.2D, 0);
			}
		}
	}
}
