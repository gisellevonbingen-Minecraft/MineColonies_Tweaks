package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecolonies.api.items.IMinecoloniesFoodItem;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.FoodUtils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksTags;

@Mixin(value = FoodUtils.class, remap = false)
public abstract class FoodUtilsMixin
{
	@Redirect(method = "getFoodValue(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;D)D", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", remap = true))
	private static Item getFoodValue(ItemStack foodStack)
	{
		var item = foodStack.getItem();

		if (item instanceof IMinecoloniesFoodItem)
		{
			return item;
		}

		var tier = MCTweaksTags.Items.getFoodTier(foodStack::is);

		if (tier == 3)
		{
			return ModItems.hand_pie;
		}
		else if (tier == 2)
		{
			return ModItems.manchet_bread;
		}
		else if (tier == 1)
		{
			return ModItems.cheddar_cheese;
		}
		else
		{
			return item;
		}

	}

}
