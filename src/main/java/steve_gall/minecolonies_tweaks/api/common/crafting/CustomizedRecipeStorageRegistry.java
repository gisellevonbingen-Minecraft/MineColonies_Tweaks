package steve_gall.minecolonies_tweaks.api.common.crafting;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.api.common.SimpleObjectRegistry;

public class CustomizedRecipeStorageRegistry extends SimpleObjectRegistry<ICustomizedRecipeStorage>
{
	public static final CustomizedRecipeStorageRegistry INSTANCE = new CustomizedRecipeStorageRegistry();

	@Override
	protected ResourceLocation getId(ICustomizedRecipeStorage object)
	{
		return object.getId();
	}

}
