package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecolonies.core.items.ItemFood;

import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = ItemFood.class, remap = false)
public abstract class ItemFoodMixin
{
	@ModifyExpressionValue(method = "getUseDuration", remap = true, at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I", remap = false))
	private int getUseDuration_foodTierMultiplier(int multiplier)
	{
		if (MCTweaksConfigServer.INSTANCE.items.disableFoodTierEatingTimeMultiplier.get())
		{
			return 1;
		}

		return multiplier;
	}

}
