package steve_gall.minecolonies_tweaks.api.common.building.module;

import net.minecraft.world.item.ItemStack;

public interface IMaximumStockEntry
{
	ItemStack stack();

	int quantity();
}
