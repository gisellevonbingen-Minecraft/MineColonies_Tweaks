package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.happiness.ExpirationBasedHappinessModifier;
import com.minecolonies.api.entity.citizen.happiness.StaticHappinessSupplier;
import com.minecolonies.api.items.IMinecoloniesFoodItem;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.constant.HappinessConstants;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksTags;

@Mixin(value = ItemStackUtils.class, remap = false)
public abstract class ItemStackUtilsMixin
{
	@Inject(method = "consumeFood", remap = false, at = @At(value = "HEAD"), cancellable = false)
	private static void consumeFood(ItemStack foodStack, AbstractEntityCitizen citizen, Inventory inventory, CallbackInfo ci)
	{
		if (!(foodStack.getItem() instanceof IMinecoloniesFoodItem) && MCTweaksTags.Items.getFoodTier(foodStack::is) >= 3)
		{
			citizen.getCitizenData().getCitizenHappinessHandler().addModifier(new ExpirationBasedHappinessModifier(HappinessConstants.HADGREATFOOD, 2.0, new StaticHappinessSupplier(2.0D), 5));
		}

	}

}
