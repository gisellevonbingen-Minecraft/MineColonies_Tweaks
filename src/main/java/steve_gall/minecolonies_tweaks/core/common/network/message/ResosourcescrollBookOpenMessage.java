package steve_gall.minecolonies_tweaks.core.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.core.common.CuriosCompat;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.item.ItemResourceScrollBook;
import steve_gall.minecolonies_tweaks.core.common.network.AbstractMessage;

public class ResosourcescrollBookOpenMessage extends AbstractMessage
{
	private final boolean request;
	private final ItemStack stack;

	public ResosourcescrollBookOpenMessage()
	{
		this.request = true;
		this.stack = ItemStack.EMPTY;
	}

	public ResosourcescrollBookOpenMessage(ItemStack stack)
	{
		this.request = false;
		this.stack = stack.copy();
	}

	public ResosourcescrollBookOpenMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.request = buffer.readBoolean();
		this.stack = buffer.readItem();
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeBoolean(this.request);
		buffer.writeItem(this.stack);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		super.handle(context);

		if (this.request)
		{
			var player = context.getSender();
			var stack = this.findResourcescrollBook(player);

			if (!stack.isEmpty())
			{
				MineColoniesTweaks.network().sendToPlayer(new ResosourcescrollBookOpenMessage(stack), player);
			}

		}
		else if (this.stack.getItem() instanceof ItemResourceScrollBook item)
		{
			item.openWindow(this.stack);
		}

	}

	public ItemStack findResourcescrollBook(Player player)
	{
		var inventory = player.getInventory();

		for (var i = 0; i < inventory.getContainerSize(); i++)
		{
			var stack = inventory.getItem(i);

			if (this.testResourcescrollBook(stack))
			{
				return stack;
			}

		}

		if (ModList.get().isLoaded(CuriosCompat.MOD_ID))
		{
			var handler = CuriosCompat.getEquippedCurios(player);

			if (handler != null)
			{
				for (var i = 0; i < handler.getSlots(); i++)
				{
					var stack = handler.getStackInSlot(i);

					if (this.testResourcescrollBook(stack))
					{
						return stack;
					}

				}

			}

		}

		return ItemStack.EMPTY;
	}

	public boolean testResourcescrollBook(ItemStack stack)
	{
		return stack.getItem() instanceof ItemResourceScrollBook;
	}

	public boolean isRequest()
	{
		return this.request;
	}

	public ItemStack getStack()
	{
		return this.stack.copy();
	}

}
