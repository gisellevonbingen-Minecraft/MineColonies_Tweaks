package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.item.FoodUtils2;

@Mixin(targets = "com.minecolonies.core.client.gui.modules.building.RestaurantMenuModuleWindow$2", remap = false)
public abstract class RestaurantMenuModuleWindow2Mixin
{
	@Redirect(method = "updateElement", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", remap = true, ordinal = 1))
	private Item updateElement_getItem(ItemStack foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack);
	}

}
