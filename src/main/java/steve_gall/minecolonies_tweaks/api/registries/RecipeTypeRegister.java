package steve_gall.minecolonies_tweaks.api.registries;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.crafting.AbstractRecipeType;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.crafting.registry.RecipeTypeEntry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import steve_gall.minecolonies_tweaks.api.common.crafting.OutputDisplayStackModifier;

public class RecipeTypeRegister
{
	@NotNull
	private final DeferredRegister<RecipeTypeEntry> recipeTypeEntires;

	public RecipeTypeRegister(@NotNull String modid)
	{
		this.recipeTypeEntires = DeferredRegisterHelper.recipeTypeEntries(modid);
	}

	public void register(@NotNull IEventBus bus)
	{
		this.getRecipeTypeEntries().register(bus);
	}

	public DeferredHolder<RecipeTypeEntry, RecipeTypeEntry> registerBuilder(@NotNull String name, @NotNull BiConsumer<ResourceLocation, RecipeTypeEntry.Builder> consumer)
	{
		return DeferredRegisterHelper.registerRecipeTypeEntry(this.getRecipeTypeEntries(), name, consumer);
	}

	public DeferredHolder<RecipeTypeEntry, RecipeTypeEntry> registerProducer(@NotNull String name, @NotNull BiFunction<ResourceLocation, IRecipeStorage, AbstractRecipeType<IRecipeStorage>> producer)
	{
		return this.registerBuilder(name, (id, builder) ->
		{
			builder.setRecipeTypeProducer(r -> producer.apply(id, r));
		});
	}

	public DeferredHolder<RecipeTypeEntry, RecipeTypeEntry> registerOutputStacks(@NotNull String name, @NotNull Function<IRecipeStorage, List<ItemStack>> outputDisplayStacksFunc)
	{
		return this.registerProducer(name, (id, recipe) ->
		{
			return new OutputDisplayStackModifier(id, recipe)
			{
				@Override
				protected List<ItemStack> collectOutputDisplayStack(IRecipeStorage recipe)
				{
					return outputDisplayStacksFunc.apply(recipe);
				}
			};
		});
	}

	@NotNull
	public DeferredRegister<RecipeTypeEntry> getRecipeTypeEntries()
	{
		return this.recipeTypeEntires;
	}

}
