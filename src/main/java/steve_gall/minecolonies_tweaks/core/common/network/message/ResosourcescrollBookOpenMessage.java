package steve_gall.minecolonies_tweaks.core.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
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
			var inventory = player.getInventory();

			for (var i = 0; i < inventory.getContainerSize(); i++)
			{
				var stack = inventory.getItem(i);

				if (stack.getItem() instanceof ItemResourceScrollBook)
				{
					MineColoniesTweaks.network().sendToPlayer(new ResosourcescrollBookOpenMessage(stack), player);
					break;
				}

			}

		}
		else if (this.stack.getItem() instanceof ItemResourceScrollBook item)
		{
			item.openWindow(this.stack);
		}

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
