package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import static com.minecolonies.api.util.constant.HappinessConstants.HADGREATFOOD;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.happiness.ExpirationBasedHappinessModifier;
import com.minecolonies.api.entity.citizen.happiness.StaticHappinessSupplier;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.core.entity.ai.minimal.EntityAIEatTask;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksTags;

@Mixin(value = EntityAIEatTask.class, remap = false)
public abstract class EntityAIEatTaskMixin
{
	@Redirect(method = "eat", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/util/ItemStackUtils.consumeFood"))
	private void eat_consumeFood(ItemStack foodStack, AbstractEntityCitizen citizen, Inventory inventory)
	{
		if (foodStack.is(MCTweaksTags.Items.GREAT_FOOD))
		{
			citizen.getCitizenData().getCitizenHappinessHandler().addModifier(new ExpirationBasedHappinessModifier(HADGREATFOOD, 2.0, new StaticHappinessSupplier(2.0), 5));
		}

		ItemStackUtils.consumeFood(foodStack, citizen, inventory);
	}

}
