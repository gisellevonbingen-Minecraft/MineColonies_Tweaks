package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.google.common.collect.EvictingQueue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenFoodHandler.CitizenFoodStats;
import com.minecolonies.api.items.IMinecoloniesFoodItem;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenFoodHandler;

import net.minecraft.world.item.Item;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksTags;

@Mixin(value = CitizenFoodHandler.class, remap = false)
public abstract class CitizenFoodHandlerMixin
{
	@Shadow(remap = false)
	private EvictingQueue<Item> lastEatenFoods;

	@SuppressWarnings("deprecation")
	@WrapOperation(method = "getFoodHappinessStats", remap = false, at = @At(value = "NEW", target = "com/minecolonies/api/entity/citizen/citizenhandlers/ICitizenFoodHandler$CitizenFoodStats"))
	private CitizenFoodStats getFoodHappinessStats(int diversity, int quality, Operation<CitizenFoodStats> operation)
	{
		for (var foodItem : this.lastEatenFoods)
		{
			if (foodItem instanceof IMinecoloniesFoodItem)
			{
				continue;
			}
			else if (MCTweaksTags.Items.getFoodTier(foodItem.builtInRegistryHolder()::is) > 0)
			{
				quality++;
			}

		}

		return operation.call(diversity, quality);
	}

}
