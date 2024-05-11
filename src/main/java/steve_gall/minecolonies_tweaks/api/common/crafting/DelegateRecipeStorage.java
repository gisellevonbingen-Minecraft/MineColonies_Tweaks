package steve_gall.minecolonies_tweaks.api.common.crafting;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.crafting.RecipeStorage;
import com.minecolonies.api.util.constant.IToolType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class DelegateRecipeStorage extends RecipeStorage
{
	private final CustomizableRecipeStorage parent;

	public DelegateRecipeStorage(CustomizableRecipeStorage parent, IToken<?> token, List<ItemStorage> input, int gridSize, @NotNull ItemStack primaryOutput, Block intermediate, ResourceLocation source, ResourceLocation type, List<ItemStack> altOutputs, List<ItemStack> secOutputs, ResourceLocation lootTable, IToolType requiredTool)
	{
		super(token, input, gridSize, primaryOutput, intermediate, source, type, altOutputs, secOutputs, lootTable, requiredTool);
		this.parent = parent;
	}

	public CustomizableRecipeStorage getParent()
	{
		return this.parent;
	}

}
