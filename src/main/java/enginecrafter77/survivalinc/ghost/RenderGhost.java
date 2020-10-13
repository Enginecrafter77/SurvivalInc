package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGhost extends RenderLivingBase<EntityPlayer> {
	public final ResourceLocation texture;
	
	public RenderGhost()
	{
		this(new ModelGhast(), new ResourceLocation("textures/entity/ghast/ghast.png"));
	}
	
	public RenderGhost(ModelBase model, ResourceLocation texture)
	{
		super(Minecraft.getMinecraft().getRenderManager(), model, 0.7F);
		this.texture = texture;
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityPlayer entity)
	{
		return this.texture;
	}

	@Override
	public void doRender(EntityPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		StatTracker tracker = entity.getCapability(StatCapability.target, null);
		GhostEnergyRecord record = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
		float opacity = record.getValue() / record.valuerange.upperEndpoint();
		
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.8F, 0.8F, 0.8F);
		if(opacity < 0.9F)
		{
			GlStateManager.color(1F, 1F, 1F, opacity);
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.depthMask(false);
			super.doRender(entity, x, y + 1, z, entityYaw, partialTicks);
			GlStateManager.depthMask(true);
			GlStateManager.disableNormalize();
			GlStateManager.disableBlend();
		}
		else super.doRender(entity, x, y + 1, z, entityYaw, partialTicks);
		GlStateManager.popMatrix();
	}

	@SubscribeEvent
	public void renderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		EntityPlayer player = event.getEntityPlayer();
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		GhostEnergyRecord record = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
		
		if(record.isActive())
		{
			this.doRender(player, event.getX(), event.getY(), event.getZ(), player.renderYawOffset, event.getPartialRenderTick());
			event.setCanceled(true);
		}
	}
}
