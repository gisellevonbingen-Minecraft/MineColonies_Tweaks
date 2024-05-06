package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.core.research.GlobalResearchBranch;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = GlobalResearchBranch.class, remap = false)
public abstract class GlobalResearchBranchMixin
{
	@Inject(method = "getBaseTime", remap = false, at = @At(value = "RETURN"), cancellable = true)
	private void getBaseTime(int depth, CallbackInfoReturnable<Integer> cir)
	{
		var original = cir.getReturnValue().doubleValue();
		var speed = MineColoniesTweaksConfigServer.INSTANCE.researches.speed.get().doubleValue();

		if (speed == 0.0D)
		{
			cir.setReturnValue(Integer.MAX_VALUE);
		}
		else
		{
			cir.setReturnValue((int) (original / speed));
		}

	}

}
