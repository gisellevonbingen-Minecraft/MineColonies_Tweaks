package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;

import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = AbstractEntityMinecoloniesRaider.class, remap = false)
public abstract class AbstractEntityMinecoloniesRaiderMixin
{
	@Shadow(remap = false)
	private int envDamageInterval;
	@Shadow(remap = false)
	private boolean envDamageImmunity;
	@Shadow(remap = false)
	private boolean tempEnvDamageImmunity;

	@Inject(method = "initStatsFor", remap = false, at = @At(value = "HEAD"), cancellable = false)
	private void initStatsFor(double baseHealth, double difficulty, double baseDamage, CallbackInfo ci)
	{
		if (MCTweaksConfigServer.INSTANCE.monsters.disableImmunity.get())
		{
			this.envDamageInterval = 0;
			this.envDamageImmunity = false;
			this.tempEnvDamageImmunity = false;
		}

	}

	@Inject(method = "setEnvDamageInterval", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void setEnvDamageInterval(int interval, CallbackInfo ci)
	{
		if (interval > 0 && MCTweaksConfigServer.INSTANCE.monsters.disableImmunity.get())
		{
			ci.cancel();
		}

	}

	@Inject(method = "setEnvDamageImmunity", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void setEnvDamageImmunity(boolean immunity, CallbackInfo ci)
	{
		if (immunity && MCTweaksConfigServer.INSTANCE.monsters.disableImmunity.get())
		{
			ci.cancel();
		}

	}

	@Inject(method = "setTempEnvDamageImmunity", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void setTempEnvDamageImmunity(boolean immunity, CallbackInfo ci)
	{
		if (immunity && MCTweaksConfigServer.INSTANCE.monsters.disableImmunity.get())
		{
			ci.cancel();
		}

	}

}
