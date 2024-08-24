package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.core.client.gui.AbstractWindowSkeleton;

import steve_gall.minecolonies_tweaks.core.client.gui.CloseableWindowExtension;

@Mixin(value = AbstractWindowSkeleton.class, remap = false)
public abstract class AbstractWindowSkeletonMixin
{
	@Inject(method = "close", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void close_Head(CallbackInfo ci)
	{
		CloseableWindowExtension.find(this).ifPresent(t ->
		{
			ci.cancel();
			t.minecolonies_tweaks$showParent();
		});
	}

}
