package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.minecolonies.api.crafting.RecipeStorage;

import steve_gall.minecolonies_tweaks.api.common.crafting.ICustomizedRecipeStorage;
import steve_gall.minecolonies_tweaks.core.common.crafting.RecipeStorageExtension;

@Mixin(value = RecipeStorage.class, remap = false)
public abstract class RecipeStorageMixin implements RecipeStorageExtension
{
	@Unique
	private ICustomizedRecipeStorage minecolonies_tweaks$customized;

	@Override
	public ICustomizedRecipeStorage minecolonies_tweaks$getCustomized()
	{
		return this.minecolonies_tweaks$customized;
	}

	@Override
	public void minecolonies_tweaks$setCustomized(ICustomizedRecipeStorage value)
	{
		this.minecolonies_tweaks$customized = value;
	}

}
