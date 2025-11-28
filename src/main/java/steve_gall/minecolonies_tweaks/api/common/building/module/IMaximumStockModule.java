package steve_gall.minecolonies_tweaks.api.common.building.module;

import java.util.List;

import com.minecolonies.api.colony.buildings.modules.IBuildingModule;

import net.minecraft.world.item.ItemStack;

public interface IMaximumStockModule extends IBuildingModule
{
	List<IMaximumStockEntry> getMaximumStocks();

	void removeMaximumStock(ItemStack itemStack);

	void addMaximumStock(ItemStack itemStack, int quantity);

	boolean isMaximumStocked(ItemStack stack);

	default boolean hasMaximumStockReachedLimit()
	{
		return this.getMaximumStockCount() >= this.getMaximumStockLimit();
	}

	int getMaximumStockCount();

	int getMaximumStockLimit();
}
