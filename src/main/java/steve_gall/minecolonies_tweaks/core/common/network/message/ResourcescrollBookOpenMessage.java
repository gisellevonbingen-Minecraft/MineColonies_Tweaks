package steve_gall.minecolonies_tweaks.core.common.network.message;

import java.util.ArrayList;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.core.common.CuriosCompat;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.item.ItemResourceScrollBook;
import steve_gall.minecolonies_tweaks.core.common.item.ItemSerializationHelper;

public class ResourcescrollBookOpenMessage extends AbstractMessage
{
	public static final CustomPacketPayload.Type<ResourcescrollBookOpenMessage> TYPE = new CustomPacketPayload.Type<>(MineColoniesTweaks.rl("resourcescroll_book_open"));

	private final boolean request;
	private final ItemStack stack;

	public ResourcescrollBookOpenMessage()
	{
		this.request = true;
		this.stack = ItemStack.EMPTY;
	}

	public ResourcescrollBookOpenMessage(ItemStack stack)
	{
		this.request = false;
		this.stack = stack.copy();
	}

	public ResourcescrollBookOpenMessage(RegistryFriendlyByteBuf buffer)
	{
		super(buffer);

		this.request = buffer.readBoolean();
		this.stack = ItemSerializationHelper.deserialize(buffer);
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeBoolean(this.request);
		ItemSerializationHelper.serialize(buffer, this.stack);
	}

	@Override
	public void handle(IPayloadContext context)
	{
		super.handle(context);

		if (this.request)
		{
			var player = context.player();
			var stack = this.findResourcescrollBook(player);

			if (!stack.isEmpty())
			{
				context.reply(new ResourcescrollBookOpenMessage(stack));
			}

		}
		else if (this.stack.getItem() instanceof ItemResourceScrollBook item)
		{
			item.openWindow(context.player().registryAccess(), this.stack);
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

	@Override
	public CustomPacketPayload.Type<ResourcescrollBookOpenMessage> type()
	{
		return TYPE;
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
