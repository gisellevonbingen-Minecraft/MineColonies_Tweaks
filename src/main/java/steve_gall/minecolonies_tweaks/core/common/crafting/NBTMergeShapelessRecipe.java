package steve_gall.minecolonies_tweaks.core.common.crafting;

import com.google.gson.JsonObject;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import steve_gall.minecolonies_tweaks.core.common.init.ModRecipes;

public class NBTMergeShapelessRecipe extends ShapelessRecipe
{
	public NBTMergeShapelessRecipe(ShapelessRecipe parent)
	{
		super(parent.getId(), parent.getGroup(), parent.category(), parent.getResultItem(null), parent.getIngredients());
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess)
	{
		var size = container.getContainerSize();
		CompoundTag mergedTag = null;

		for (var i = 0; i < size; i++)
		{
			var base = container.getItem(i);
			var tag = base.getTag();

			if (tag == null)
			{
				continue;
			}
			else if (mergedTag == null)
			{
				mergedTag = tag.copy();
			}
			else
			{
				mergedTag.merge(tag);
			}

		}

		var assemble = super.assemble(container, registryAccess);

		if (mergedTag != null)
		{
			assemble.setTag(mergedTag.copy());
		}

		return assemble;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return ModRecipes.NBT_MERGE_SHAPELESS_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<NBTMergeShapelessRecipe>
	{
		@Override
		public NBTMergeShapelessRecipe fromJson(ResourceLocation id, JsonObject json)
		{
			var parent = RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json);
			return new NBTMergeShapelessRecipe(parent);
		}

		@Override
		public NBTMergeShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
		{
			var parent = RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buffer);
			return new NBTMergeShapelessRecipe(parent);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, NBTMergeShapelessRecipe parent)
		{
			RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, parent);
		}

	}

}
