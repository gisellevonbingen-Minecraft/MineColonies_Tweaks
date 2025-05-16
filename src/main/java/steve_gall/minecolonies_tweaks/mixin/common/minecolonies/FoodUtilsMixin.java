package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.FoodUtils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.item.FoodUtils2;

@Mixin(value = FoodUtils.class, remap = false)
public abstract class FoodUtilsMixin
{
	@Redirect(method = "getFoodValue(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;D)D", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", remap = true))
	private static Item getFoodValue(ItemStack foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack);
	}

	@Redirect(method = "hasBestOptionInInv", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", remap = true, ordinal = 0))
	private static Item hasBestOptionInInv0(ItemStack foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack);
	}

	@Redirect(method = "hasBestOptionInInv", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/crafting/ItemStorage;getItem()Lnet/minecraft/world/item/Item;", remap = false, ordinal = 0))
	private static Item hasBestOptionInInv0(ItemStorage foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack.getItemStack());
	}

	@Redirect(method = "hasBestOptionInInv", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/crafting/ItemStorage;getItem()Lnet/minecraft/world/item/Item;", remap = false, ordinal = 2))
	private static Item hasBestOptionInInv2(ItemStorage foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack.getItemStack());
	}

	@Redirect(method = "getBestFoodForCitizen", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/crafting/ItemStorage;getItem()Lnet/minecraft/world/item/Item;", remap = false, ordinal = 0))
	private static Item getBestFoodForCitizen0(ItemStorage foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack.getItemStack());
	}

	@Redirect(method = "getBestFoodForCitizen", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/crafting/ItemStorage;getItem()Lnet/minecraft/world/item/Item;", remap = false, ordinal = 3))
	private static Item getBestFoodForCitizen3(ItemStorage foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack.getItemStack());
	}

	@Redirect(method = "checkForFoodInBuilding", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/crafting/ItemStorage;getItem()Lnet/minecraft/world/item/Item;", remap = false, ordinal = 0))
	private static Item checkForFoodInBuilding0(ItemStorage foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack.getItemStack());
	}

	@Redirect(method = "checkForFoodInBuilding", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/crafting/ItemStorage;getItem()Lnet/minecraft/world/item/Item;", remap = false, ordinal = 2))
	private static Item checkForFoodInBuilding2(ItemStorage foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack.getItemStack());
	}

}
