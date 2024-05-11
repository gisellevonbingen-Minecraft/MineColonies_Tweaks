package steve_gall.minecolonies_tweaks.api.common.crafting;

import java.util.List;

import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.constant.IToolType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import steve_gall.minecolonies_tweaks.apiimpl.common.crafting.CustomizableRecipeStorageFactory;

public interface ICustomizedRecipeStorage
{
	ResourceLocation getId();

	List<ItemStorage> getInput();

	int getGridSize();

	ItemStack getPrimaryOutput();

	List<ItemStack> getAlternateOutputs();

	List<ItemStack> getSecondaryOutputs();

	Block getIntermediate();

	ResourceLocation getRecipeSource();

	ResourceLocation getRecipeType();

	ResourceLocation getLootTable();

	IToolType getRequiredTool();

	default IRecipeStorage wrap()
	{
		var controller = StandardFactoryController.getInstance();
		var outputType = CustomizableRecipeStorageFactory.OUTPUT_TYPE;
		var inputType = CustomizableRecipeStorageFactory.INPUT_TYPE;
		var input = controller.getNewInstance(inputType);
		return controller.getNewInstance(outputType, input, this);
	}

}
