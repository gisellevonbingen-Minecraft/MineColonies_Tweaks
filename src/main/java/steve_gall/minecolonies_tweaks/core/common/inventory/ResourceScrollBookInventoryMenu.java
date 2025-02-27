package steve_gall.minecolonies_tweaks.core.common.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksItems;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksMenuTypes;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksTags;
import steve_gall.minecolonies_tweaks.core.common.item.ItemResourceScrollBook;

public class ResourceScrollBookInventoryMenu extends AbstractContainerMenu
{
	private final Inventory inventory;
	private final int bookSlot;
	private final ItemResourceScrollBook.Container container;

	public static ResourceScrollBookInventoryMenu create(int windowId, Inventory inventory, FriendlyByteBuf extraData)
	{
		var bookSlot = extraData.readInt();
		return new ResourceScrollBookInventoryMenu(windowId, inventory, bookSlot);
	}

	public ResourceScrollBookInventoryMenu(int windowId, Inventory inventory, int bookSlot)
	{
		super(MCTweaksMenuTypes.RESOURCESCROLL_BOOK_INVENTORY.get(), windowId);

		this.inventory = inventory;
		this.bookSlot = bookSlot;
		this.container = MCTweaksItems.RESOURCESCROLL_BOOK.get().getContainer(this.getBook());

		for (var i = 0; i < this.container.getContainerSize(); i++)
		{
			var xi = i % 9;
			var yi = i / 9;
			this.addSlot(new BookSlot(this.container, i, 8 + xi * 18, 18 + yi * 18));
		}

		var i = (((this.container.getContainerSize() + 8) / 9) - 4) * 18;

		for (var yi = 0; yi < 3; yi++)
		{
			for (var xi = 0; xi < 9; xi++)
			{
				var slot = xi + (yi + 1) * 9;
				this.addSlot(new InventorySlot(inventory, slot, 8 + xi * 18, 103 + yi * 18 + i));
			}

		}

		for (var xi = 0; xi < 9; ++xi)
		{
			var slot = xi;
			this.addSlot(new InventorySlot(inventory, slot, 8 + xi * 18, 161 + i));
		}

	}

	public ItemStack getBook()
	{
		return this.inventory.getItem(this.getBookSlot());
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex)
	{
		var itemstack = ItemStack.EMPTY;
		var slot = this.slots.get(slotIndex);

		if (slot != null && slot.hasItem())
		{
			var stack = slot.getItem();
			itemstack = stack.copy();

			if (slotIndex < this.container.getContainerSize())
			{
				if (!this.moveItemStackTo(stack, this.container.getContainerSize(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}

			}
			else if (!this.moveItemStackTo(stack, 0, this.container.getContainerSize(), false))
			{
				return ItemStack.EMPTY;
			}

			if (stack.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}

		}

		return itemstack;
	}

	public ItemResourceScrollBook.Container getContainer()
	{
		return this.container;
	}

	public int getBookSlot()
	{
		return this.bookSlot;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return this.getBook().is(MCTweaksItems.RESOURCESCROLL_BOOK.get());
	}

	public class BookSlot extends Slot
	{
		public BookSlot(ItemResourceScrollBook.Container container, int slot, int x, int y)
		{
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return stack.is(MCTweaksTags.Items.RESOURCESCROLLBOOK_ELEMENT);
		}

	}

	public class InventorySlot extends Slot
	{
		public InventorySlot(Inventory inventory, int slot, int x, int y)
		{
			super(inventory, slot, x, y);
		}

		@Override
		public boolean mayPickup(Player player)
		{
			return this.getSlotIndex() != getBookSlot();
		}

	}

}
