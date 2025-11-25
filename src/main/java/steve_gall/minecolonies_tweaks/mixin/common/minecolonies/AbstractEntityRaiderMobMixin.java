package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.entity.mobs.AbstractEntityRaiderMob;

import net.minecraft.world.damagesource.DamageSource;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = AbstractEntityRaiderMob.class, remap = false)
public abstract class AbstractEntityRaiderMobMixin
{
	@Shadow(remap = false)
	private int envDmgCooldown;
	@Shadow(remap = false)
	private boolean envDamageImmunity;
	@Shadow(remap = false)
	private boolean tempEnvDamageImmunity;

	@Inject(method = "hurt", remap = false, at = @At(value = "HEAD"), cancellable = false)
	private void hurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir)
	{
		if (MCTweaksConfigServer.INSTANCE.monsters.disableImmunity.get())
		{
			this.envDmgCooldown = 0;
			this.envDamageImmunity = false;
			this.tempEnvDamageImmunity = false;
		}

	}

	@ModifyConstant(method = "hurt", remap = true, constant = @Constant(floatValue = 30.0F))
	private float hurt_MIN_THORNS_DAMAG(float MIN_THORNS_DAMAGE)
	{
		if (MCTweaksConfigServer.INSTANCE.monsters.disableThorns.get())
		{
			return Float.POSITIVE_INFINITY;
		}
		else
		{
			return MIN_THORNS_DAMAGE;
		}

	}

}
