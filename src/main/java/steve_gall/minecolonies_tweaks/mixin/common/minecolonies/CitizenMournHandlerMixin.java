package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenMournHandler;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = CitizenMournHandler.class, remap = false)
public abstract class CitizenMournHandlerMixin
{
	@Inject(method = "addDeceasedCitizen", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void addDeceasedCitizen(String name, CallbackInfo ci)
	{
		if (MineColoniesTweaksConfigServer.INSTANCE.citizens.disableMourn.get().booleanValue())
		{
			ci.cancel();
		}

	}

}
