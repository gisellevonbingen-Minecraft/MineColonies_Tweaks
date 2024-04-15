package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;

public interface IDeliverableObject extends IRequestableObject
{
	@NotNull
	IDeliverableObject copyWithCount(int newCount);

	int getCount();

	default int getMinimumCount()
	{
		return this.getCount();
	}

	boolean matches(@NotNull ItemStack stack);
}
