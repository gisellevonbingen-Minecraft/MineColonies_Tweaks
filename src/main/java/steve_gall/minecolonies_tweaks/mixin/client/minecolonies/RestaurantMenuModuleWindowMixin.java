package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import java.util.List;
import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.items.IMinecoloniesFoodItem;
import com.minecolonies.core.client.gui.modules.building.RestaurantMenuModuleWindow;

import net.minecraft.world.item.Item;
import steve_gall.minecolonies_tweaks.core.common.item.FoodUtils2;

@Mixin(value = RestaurantMenuModuleWindow.class, remap = false)
public abstract class RestaurantMenuModuleWindowMixin
{
	@Redirect(method = "updateStockList", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/crafting/ItemStorage;getItem()Lnet/minecraft/world/item/Item;", remap = false))
	private Item updateStockList_getItem(ItemStorage foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack.getItemStack());
	}

	@Inject(method = "applySorting", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void applySorting(List<ItemStorage> displayedList, CallbackInfo ci)
	{
		ci.cancel();
		displayedList.sort((o1, o2) ->
		{
			int score1 = FoodUtils2.getTierRepresentedFood(o1.getItemStack()) instanceof IMinecoloniesFoodItem foodItem ? foodItem.getTier() * -100 : -o1.getItemStack().getFoodProperties(null).getNutrition();
			int score2 = FoodUtils2.getTierRepresentedFood(o2.getItemStack()) instanceof IMinecoloniesFoodItem foodItem2 ? foodItem2.getTier() * -100 : -o2.getItemStack().getFoodProperties(null).getNutrition();

			final int scoreComparison = Integer.compare(score1, score2);
			if (scoreComparison != 0)
			{
				return scoreComparison;
			}

			return o1.getItemStack().getDisplayName().getString().toLowerCase(Locale.US).compareTo(o2.getItemStack().getDisplayName().getString().toLowerCase(Locale.US));
		});
	}

}
