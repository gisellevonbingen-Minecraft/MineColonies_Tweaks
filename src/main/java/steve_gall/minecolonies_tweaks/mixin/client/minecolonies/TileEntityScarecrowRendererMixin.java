package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.blocks.huts.AbstractBlockMinecoloniesDefault;
import com.minecolonies.api.tileentities.AbstractTileEntityScarecrow;
import com.minecolonies.core.client.render.TileEntityScarecrowRenderer;
import com.minecolonies.core.colony.fields.FarmField;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import steve_gall.minecolonies_tweaks.core.common.colony.IColonyViewExtension;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;

@Mixin(value = TileEntityScarecrowRenderer.class, remap = false)
public abstract class TileEntityScarecrowRendererMixin
{
	@Inject(method = "render", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void render(AbstractTileEntityScarecrow te, float partialTicks, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int lightA, int lightB, CallbackInfo ci)
	{
		if (MineColoniesTweaksConfigClient.INSTANCE.renderFieldSeed.get())
		{
			this.minecolonies_tweaks$renderSeed(te, matrixStack, iRenderTypeBuffer, lightA, lightB);
		}

	}

	@Unique
	private void minecolonies_tweaks$renderSeed(AbstractTileEntityScarecrow te, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int lightA, int lightB)
	{
		if (te.getCurrentColony() instanceof IColonyViewExtension colonyView && colonyView.minecolonies_tweaks$getField(te.getBlockPos()) instanceof FarmField field)
		{
			var facing = te.getLevel().getBlockState(te.getBlockPos()).getValue(AbstractBlockMinecoloniesDefault.FACING);

			matrixStack.pushPose();
			matrixStack.translate(0.5D, 2.5D, 0.5D);
			matrixStack.mulPose(facing.getRotation());
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			matrixStack.scale(0.5F, 0.5F, 0.5F);

			var itemRenderer = Minecraft.getInstance().getItemRenderer();
			itemRenderer.renderStatic(field.getSeed(), ItemTransforms.TransformType.FIXED, lightA, lightB, matrixStack, iRenderTypeBuffer, OverlayTexture.NO_OVERLAY);

			matrixStack.popPose();
		}

	}

}
