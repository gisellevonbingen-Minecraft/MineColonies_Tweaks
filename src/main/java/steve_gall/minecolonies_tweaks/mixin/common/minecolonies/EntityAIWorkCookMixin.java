package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.core.entity.ai.workers.service.EntityAIWorkCook;

import net.minecraft.world.item.Item;
import steve_gall.minecolonies_tweaks.core.common.item.FoodUtils2;

@Mixin(value = EntityAIWorkCook.class, remap = false)
public abstract class EntityAIWorkCookMixin
{
	@Redirect(method = "checkForImportantJobs", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/crafting/ItemStorage;getItem()Lnet/minecraft/world/item/Item;", remap = false, ordinal = 0))
	private Item checkForImportantJobs(ItemStorage foodStack)
	{
		return FoodUtils2.getTierRepresentedFood(foodStack.getItemStack());
	}

}
