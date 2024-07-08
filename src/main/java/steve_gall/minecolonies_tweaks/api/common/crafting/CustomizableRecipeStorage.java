package steve_gall.minecolonies_tweaks.api.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ldtteam.structurize.items.ModItems;
import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.AbstractRecipeType;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.crafting.RecipeStorage;
import com.minecolonies.api.util.constant.IToolType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.items.IItemHandler;

public class CustomizableRecipeStorage implements ICustomizableRecipeStorage
{
	@NotNull
	private final ICustomizedRecipeStorage impl;
	@NotNull
	private final DelegateRecipeStorage delegate;
	@NotNull
	private final AbstractRecipeType<IRecipeStorage> recipeType;

	private boolean hasHashCode;
	private int hashCode;

	public CustomizableRecipeStorage(@NotNull IToken<?> token, @NotNull ICustomizedRecipeStorage impl)
	{
		this.impl = impl;
		this.delegate = new DelegateRecipeStorage(this, token, impl.getInput(), impl.getGridSize(), impl.getPrimaryOutput(), impl.getIntermediate(), impl.getRecipeSource(), impl.getRecipeType(), impl.getAlternateOutputs(), getSecondaryOutputs(impl), impl.getLootTable(), impl.getRequiredTool());

		var type = impl.getRecipeType();
		var recipeTypes = MinecoloniesAPIProxy.getInstance().getRecipeTypeRegistry();

		if (type != null && recipeTypes.containsKey(type))
		{
			this.recipeType = recipeTypes.getValue(type).getHandlerProducer().apply(this);
		}
		else
		{
			this.recipeType = recipeTypes.getValue(recipeTypes.getDefaultKey()).getHandlerProducer().apply(this);
		}

		this.hasHashCode = false;
		this.hashCode = 0;
	}

	private ArrayList<ItemStack> getSecondaryOutputs(ICustomizedRecipeStorage recipeStorage)
	{
		var secondaryOutputs = new ArrayList<ItemStack>();
		secondaryOutputs.add(new ItemStack(ModItems.buildTool.get()));
		secondaryOutputs.addAll(recipeStorage.getSecondaryOutputs());
		return secondaryOutputs;
	}

	@Override
	public int hashCode()
	{
		if (!this.hasHashCode)
		{
			this.hasHashCode = true;
			this.hashCode = Objects.hash(this.delegate, this.impl);
		}

		return this.hashCode;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (o instanceof CustomizableRecipeStorage other)
		{
			return this.delegate.equals(other.delegate) && this.impl.equals(other.impl);
		}

		return false;
	}

	@Override
	@NotNull
	public ICustomizedRecipeStorage getImpl()
	{
		return this.impl;
	}

	@Override
	public AbstractRecipeType<IRecipeStorage> getRecipeType()
	{
		return this.recipeType;
	}

	@Override
	public List<ItemStorage> getInput()
	{
		return this.delegate.getInput();
	}

	@Override
	public List<ItemStorage> getCleanedInput()
	{
		return this.delegate.getCleanedInput();
	}

	@Override
	public ItemStack getPrimaryOutput()
	{
		return this.delegate.getPrimaryOutput();
	}

	@Override
	public int getGridSize()
	{
		return this.delegate.getGridSize();
	}

	@Override
	public Block getIntermediate()
	{
		return this.delegate.getIntermediate();
	}

	@Override
	public boolean canFullFillRecipe(int qty, Map<ItemStorage, Integer> existingRequirements, @NotNull IItemHandler... inventories)
	{
		return this.delegate.canFullFillRecipe(qty, existingRequirements, inventories);
	}

	@Override
	public boolean canFullFillRecipe(int qty, Map<ItemStorage, Integer> existingRequirements, @NotNull List<IItemHandler> citizen, @NotNull IBuilding building)
	{
		return this.delegate.canFullFillRecipe(qty, existingRequirements, citizen, building);
	}

	@Override
	public @Nullable List<ItemStack> fullfillRecipeAndCopy(LootContext context, List<IItemHandler> handlers, boolean doInsert)
	{
		return this.delegate.fullfillRecipeAndCopy(context, handlers, doInsert);
	}

	@Override
	public List<ItemStack> getAlternateOutputs()
	{
		return this.delegate.getAlternateOutputs();
	}

	@Override
	public RecipeStorage getClassicForMultiOutput(ItemStack requiredOutput)
	{
		return this.delegate.getClassicForMultiOutput(requiredOutput);
	}

	@Override
	public RecipeStorage getClassicForMultiOutput(Predicate<ItemStack> stackPredicate)
	{
		return this.delegate.getClassicForMultiOutput(stackPredicate);
	}

	@Override
	public ResourceLocation getRecipeSource()
	{
		return this.delegate.getRecipeSource();
	}

	@Override
	public List<ItemStack> getSecondaryOutputs()
	{
		return this.delegate.getSecondaryOutputs();
	}

	@Override
	public List<ItemStack> getCraftingToolsAndSecondaryOutputs()
	{
		return this.delegate.getCraftingToolsAndSecondaryOutputs();
	}

	@Override
	public List<ItemStack> getCraftingTools()
	{
		return this.delegate.getCraftingTools();
	}

	@Override
	public IToolType getRequiredTool()
	{
		return this.delegate.getRequiredTool();
	}

	@Override
	public ResourceLocation getLootTable()
	{
		return this.delegate.getLootTable();
	}

	@Override
	public IToken<?> getToken()
	{
		return this.delegate.getToken();
	}

}
