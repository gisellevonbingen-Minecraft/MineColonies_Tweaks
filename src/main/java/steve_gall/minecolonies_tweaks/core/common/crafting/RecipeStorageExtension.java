package steve_gall.minecolonies_tweaks.core.common.crafting;

import steve_gall.minecolonies_tweaks.api.common.crafting.ICustomizedRecipeStorage;

public interface RecipeStorageExtension
{
	ICustomizedRecipeStorage minecolonies_tweaks$getCustomized();

	void minecolonies_tweaks$setCustomized(ICustomizedRecipeStorage value);
}
