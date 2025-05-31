package steve_gall.minecolonies_tweaks.core.common.inventory;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class BlackHoleItemHandler implements IItemHandlerModifiable
{
	public static final IItemHandlerModifiable INSTANCE = new BlackHoleItemHandler();

	@Override
	public int getSlots()
	{
		return 1;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack)
	{
		return true;
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack)
	{

	}

}
