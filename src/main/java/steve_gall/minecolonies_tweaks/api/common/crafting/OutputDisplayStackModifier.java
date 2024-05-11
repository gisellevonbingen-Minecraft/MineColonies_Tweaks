package steve_gall.minecolonies_tweaks.api.common.crafting;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.crafting.AbstractRecipeType;
import com.minecolonies.api.crafting.IRecipeStorage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class OutputDisplayStackModifier extends AbstractRecipeType<IRecipeStorage>
{
	@NotNull
	private final ResourceLocation id;
	@Nullable
	private List<ItemStack> outputDisplayStacks;

	public OutputDisplayStackModifier(@NotNull ResourceLocation id, @NotNull IRecipeStorage recipe)
	{
		super(recipe);
		this.id = id;
		this.outputDisplayStacks = null;
	}

	@Override
	@NotNull
	public List<ItemStack> getOutputDisplayStacks()
	{
		if (this.outputDisplayStacks == null)
		{
			this.outputDisplayStacks = Collections.unmodifiableList(this.collectOutputDisplayStack(this.getRecipe()));
		}

		return this.outputDisplayStacks;
	}

	protected abstract List<ItemStack> collectOutputDisplayStack(IRecipeStorage recipe);

	@Override
	@NotNull
	public ResourceLocation getId()
	{
		return this.id;
	}

}
