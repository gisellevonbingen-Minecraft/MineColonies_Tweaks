package steve_gall.minecolonies_tweaks.mixin.client.blockui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonImage;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.client.gui.ButtonImageExtension;

@Mixin(value = ButtonImage.class, remap = false)
public class ButtonImageMixin extends Button implements ButtonImageExtension
{
	@Unique
	private boolean minecolonies_tweaks$flipX;

	@Redirect(method = "drawSelf", remap = false, at = @At(value = "INVOKE", target = "blit"))
	public void drawSelf_blit(PoseStack ps, ResourceLocation rl, int x, int y, int w, int h, int u, int v, int uW, int vH, int mapW, int mapH)
	{
		if (this.minecolonies_tweaks$flipX)
		{
			blit(ps, rl, x, y, w, h, u - uW, v, -uW, vH, mapW, mapH);
		}
		else
		{
			blit(ps, rl, x, y, w, h, u, v, uW, vH, mapW, mapH);
		}

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
