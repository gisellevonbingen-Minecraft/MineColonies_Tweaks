package steve_gall.minecolonies_tweaks.core.common.crafting;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksRecipes;

public class ComponentMergeShapelessRecipe extends ShapelessRecipe
{
	public ComponentMergeShapelessRecipe(ShapelessRecipe parent)
	{
		super(parent.getGroup(), parent.category(), parent.getResultItem(null), parent.getIngredients());
	}

	@Override
	public ItemStack assemble(CraftingInput container, HolderLookup.Provider provider)
	{
		var assemble = super.assemble(container, provider);
		var size = container.size();

		for (var i = 0; i < size; i++)
		{
			var base = container.getItem(i);
			assemble.applyComponents(base.getComponentsPatch());
		}

		return assemble;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return MCTweaksRecipes.COMPONENT_MERGE_SHAPELESS_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<ComponentMergeShapelessRecipe>
	{
		private final MapCodec<ComponentMergeShapelessRecipe> codec = RecipeSerializer.SHAPELESS_RECIPE.codec().xmap(ComponentMergeShapelessRecipe::new, s -> s);
		private final StreamCodec<RegistryFriendlyByteBuf, ComponentMergeShapelessRecipe> streamCodec = RecipeSerializer.SHAPELESS_RECIPE.streamCodec().map(ComponentMergeShapelessRecipe::new, s -> s);

		@Override
		public MapCodec<ComponentMergeShapelessRecipe> codec()
		{
			return this.codec;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ComponentMergeShapelessRecipe> streamCodec()
		{
			return this.streamCodec;
		}

	}

}
