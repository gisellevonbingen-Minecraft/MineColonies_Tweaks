package steve_gall.minecolonies_tweaks.mixin.client.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.Screen;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableWindowExtension;

@Mixin(value = Screen.class, remap = true)
public abstract class ScreenMixin
{
	@Inject(method = "onClose", remap = true, at = @At(value = "TAIL"))
	private void onClose(CallbackInfo ci)
	{
		if (this instanceof CloseableWindowExtension self)
		{
			self.minecolonies_tweaks$showParent();
		}

	}

}
