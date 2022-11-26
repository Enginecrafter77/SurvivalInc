package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

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
	public void doRender(@Nonnull EntityPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		float opacity = StatCapability.obtainRecord(SurvivalInc.ghost, entity).map(SimpleStatRecord::getNormalizedValue).orElse(0F);

		if(ModConfig.CLIENT.pulsatingGhosts)
			opacity *= (0.8D + 0.15D * Math.sin(((float)entity.ticksExisted + partialTicks) / (2 * Math.PI)));
				
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.8F, 0.8F, 0.8F);
		if(ModConfig.CLIENT.pulsatingGhosts || opacity < 0.9F)
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
		if(StatCapability.obtainRecord(SurvivalInc.ghost, player).map(GhostEnergyRecord::isActive).orElse(false))
		{
			this.doRender(player, event.getX(), event.getY(), event.getZ(), player.renderYawOffset, event.getPartialRenderTick());
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void renderPlayerHand(RenderHandEvent event)
	{
		if(StatCapability.obtainRecord(SurvivalInc.ghost, Minecraft.getMinecraft().player).map(GhostEnergyRecord::isActive).orElse(false))
			event.setCanceled(true);
	}
}
