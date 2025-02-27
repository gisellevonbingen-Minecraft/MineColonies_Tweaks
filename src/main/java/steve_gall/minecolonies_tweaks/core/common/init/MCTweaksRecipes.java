package steve_gall.minecolonies_tweaks.core.common.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.crafting.NBTMergeShapelessRecipe;

public class MCTweaksRecipes
{
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MineColoniesTweaks.MOD_ID);
	public static final RegistryObject<NBTMergeShapelessRecipe.Serializer> NBT_MERGE_SHAPELESS_SERIALIZER = SERIALIZERS.register("nbt_merge_shapeless", () -> new NBTMergeShapelessRecipe.Serializer());

	private MCTweaksRecipes()
	{

	}

}
