package steve_gall.minecolonies_tweaks.api.common.crafting;

import com.minecolonies.api.crafting.IRecipeStorage;

public interface ICustomizableRecipeStorage extends IRecipeStorage
{
	ICustomizedRecipeStorage getImpl();
}
