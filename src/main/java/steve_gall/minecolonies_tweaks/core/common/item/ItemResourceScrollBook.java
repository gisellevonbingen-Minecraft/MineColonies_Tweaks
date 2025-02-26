package steve_gall.minecolonies_tweaks.core.common.item;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.creativetab.ModCreativeTabs;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import steve_gall.minecolonies_tweaks.core.client.gui.ResourceScrollBookListWindow;
import steve_gall.minecolonies_tweaks.core.common.inventory.ResourceScrollBookInventoryMenu;

public class ItemResourceScrollBook extends Item
{
	public static final List<Component> TOOLTIPS = Arrays.asList(//
			Component.translatable("item.minecolonies_tweaks.resourcescroll_book.tooltip1"), //
			Component.translatable("item.minecolonies_tweaks.resourcescroll_book.tooltip2")//
	);

	private final int slots;

	public ItemResourceScrollBook(Item.Properties properites, int slots)
	{
		super(properites.stacksTo(1).tab(ModCreativeTabs.MINECOLONIES));
		this.slots = slots;
	}

	public Container getContainer(ItemStack stack)
	{
		return new Container(stack);
	}

	public int getSlots()
	{
		return this.slots;
	}

	public void setItems(ItemStack stack, List<ItemStack> slots)
	{
		var compound = stack.getOrCreateTagElement("resourcebook");
		var slotsTag = new ListTag();
		compound.put("slots", slotsTag);

		var count = slots.size();
		var size = Math.max(count, this.slots);

		for (var i = 0; i < size; i++)
		{
			var slot = i < count ? slots.get(i) : ItemStack.EMPTY;
			slotsTag.add(slot.serializeNBT());
		}

	}

	public NonNullList<ItemStack> getItems(ItemStack stack)
	{
		var compound = stack.getTagElement("resourcebook");
		var items = NonNullList.withSize(this.slots, ItemStack.EMPTY);

		if (compound != null)
		{
			var slots = compound.getList("slots", Tag.TAG_COMPOUND);
			var size = slots.size();

			for (var i = 0; i < size; i++)
			{
				var slotTag = slots.getCompound(i);
				var slot = ItemStack.of(slotTag);
				items.set(i, slot);
			}

		}

		return items;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag)
	{
		super.appendHoverText(stack, level, tooltip, flag);

		tooltip.addAll(TOOLTIPS);
	}

	@Override
	@NotNull
	public InteractionResult useOn(UseOnContext context)
	{
		var player = context.getPlayer();
		var stack = player.getItemInHand(context.getHand());

		if (player instanceof ServerPlayer serverPlayer)
		{
			if (player.isShiftKeyDown())
			{
				this.openInventory(serverPlayer, stack, this.getHandSlot(player, context.getHand()));
			}

		}
		else
		{
			this.openWindow(stack);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var stack = player.getItemInHand(hand);

		if (player instanceof ServerPlayer serverPlayer)
		{
			if (player.isShiftKeyDown())
			{
				this.openInventory(serverPlayer, stack, this.getHandSlot(player, hand));
			}

		}
		else
		{
			this.openWindow(stack);
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	public void openWindow(ItemStack stack)
	{
		new ResourceScrollBookListWindow(this.getItems(stack), null).open();
	}

	public void openInventory(ServerPlayer player, ItemStack stack, int slot)
	{
		NetworkHooks.openScreen(player, new MenuProvider()
		{
			@Override
			public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
			{
				return new ResourceScrollBookInventoryMenu(windowId, inventory, slot);
			}

			@Override
			public Component getDisplayName()
			{
				return stack.getHoverName();
			}

		}, buf ->
		{
			buf.writeInt(slot);
		});

	}

	public int getHandSlot(Player player, InteractionHand hand)
	{
		if (hand == InteractionHand.MAIN_HAND)
		{
			return player.getInventory().selected;
		}
		else if (hand == InteractionHand.OFF_HAND)
		{
			return Inventory.SLOT_OFFHAND;
		}
		else
		{
			return -1;
		}

	}

	public class Container implements net.minecraft.world.Container
	{
		private final ItemStack stack;
		private final List<ItemStack> items;

		public Container(ItemStack stack)
		{
			this.stack = stack;
			this.items = getItems(stack);
		}

		@Override
		public int getMaxStackSize()
		{
			return 1;
		}

		@Override
		public void clearContent()
		{
			for (var i = 0; i < this.getContainerSize(); i++)
			{
				this.items.set(i, ItemStack.EMPTY);
			}

			this.setChanged();
		}

		@Override
		public int getContainerSize()
		{
			return getSlots();
		}

		@Override
		public boolean isEmpty()
		{
			for (var stack : this.items)
			{
				if (!stack.isEmpty())
				{
					return false;
				}

			}

			return true;
		}

		@Override
		public ItemStack getItem(int slot)
		{
			return this.items.get(slot);
		}

		@Override
		public ItemStack removeItem(int slot, int count)
		{
			var stack = ContainerHelper.removeItem(this.items, slot, count);

			if (!stack.isEmpty())
			{
				this.setChanged();
			}

			return stack;
		}

		@Override
		public ItemStack removeItemNoUpdate(int slot)
		{
			var stack = this.items.get(slot);

			if (stack.isEmpty())
			{
				return ItemStack.EMPTY;
			}
			else
			{
				this.items.set(slot, ItemStack.EMPTY);
				return stack;
			}

		}

		@Override
		public void setItem(int slot, ItemStack stack)
		{
			this.items.set(slot, stack);
			var maxStackSize = this.getMaxStackSize();

			if (!stack.isEmpty() && stack.getCount() > maxStackSize)
			{
				stack.setCount(maxStackSize);
			}

			this.setChanged();
		}

		@Override
		public void setChanged()
		{
			setItems(this.stack, this.items);
		}

		@Override
		public boolean stillValid(Player player)
		{
			return false;
		}

	}

}
