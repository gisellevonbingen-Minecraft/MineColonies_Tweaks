package steve_gall.minecolonies_tweaks.core.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.crafting.ComponentMergeShapelessRecipe;

public class MCTweaksRecipes
{
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MineColoniesTweaks.MOD_ID);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ComponentMergeShapelessRecipe>> COMPONENT_MERGE_SHAPELESS_SERIALIZER = SERIALIZERS.register("component_merge_shapeless", () -> new ComponentMergeShapelessRecipe.Serializer());

	private MCTweaksRecipes()
	{

	}

}
