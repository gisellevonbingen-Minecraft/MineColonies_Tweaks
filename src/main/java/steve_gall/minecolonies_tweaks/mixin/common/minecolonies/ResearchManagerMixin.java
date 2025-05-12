package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.research.IResearchEffectManager;
import com.minecolonies.core.colony.managers.ResearchManager;

import steve_gall.minecolonies_tweaks.core.common.research.ResearchEffectManagerExtension;

@Mixin(value = ResearchManager.class, remap = false)
public abstract class ResearchManagerMixin
{
	@Shadow
	private IColony colony;

	@Shadow
	private IResearchEffectManager effects;

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void init(CallbackInfo ci)
	{
		((ResearchEffectManagerExtension) this.effects).minecolonies_tweaks$setColony(this.colony);
	}

}
