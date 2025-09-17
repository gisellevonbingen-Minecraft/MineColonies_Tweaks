package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.core.colony.CitizenData;

import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = CitizenData.class, remap = false)
public abstract class CitizenDataMixin
{
	@Shadow(remap = false)
	private int leisureTime;

	@Inject(method = "update", remap = false, at = @At(value = "TAIL"), cancellable = true)
	private void update(int tickRate, CallbackInfo ci)
	{
		if (MCTweaksConfigServer.INSTANCE.jobs.disableLeisure.get())
		{
			this.leisureTime = 0;
		}

	}

}
