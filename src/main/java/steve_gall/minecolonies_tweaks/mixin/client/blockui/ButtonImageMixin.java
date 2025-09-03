package steve_gall.minecolonies_tweaks.mixin.client.blockui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import steve_gall.minecolonies_tweaks.core.client.gui.ButtonImageExtension;

@Mixin(value = ButtonImage.class, remap = false)
public class ButtonImageMixin extends Button implements ButtonImageExtension
{
	@Unique
	private boolean minecolonies_tweaks$flipX;

	@Redirect(method = "drawSelf", remap = false, at = @At(value = "INVOKE", target = "Lcom/ldtteam/blockui/UiRenderMacros$ResolvedBlit;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIII)V"))
	public void drawSelf_blit(ResolvedBlit self, PoseStack ps, int x, int y, int w, int h)
	{
		ps.pushPose();

		if (this.minecolonies_tweaks$flipX)
		{
			RenderSystem.disableCull();
			self.blit(ps, x + w, y, -w, h);
			RenderSystem.enableCull();
		}
		else
		{
			self.blit(ps, x, y, w, h);
		}

		ps.popPose();
	}

	@Override
	public boolean minecolonies_tweaks$isFlipX()
	{
		return this.minecolonies_tweaks$flipX;
	}

	@Override
	public void minecolonies_tweaks$setFlipX(boolean flipX)
	{
		this.minecolonies_tweaks$flipX = flipX;
	}

}
