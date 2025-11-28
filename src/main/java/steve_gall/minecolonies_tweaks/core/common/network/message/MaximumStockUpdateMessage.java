package steve_gall.minecolonies_tweaks.core.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.api.common.building.module.IMaximumStockModule;
import steve_gall.minecolonies_tweaks.api.common.building.module.IMaximumStockModuleView;

public class MaximumStockUpdateMessage extends BuildingModuleMessage
{
	private final ItemStack stack;
	private final boolean add;
	private final int quantity;

	public static MaximumStockUpdateMessage add(IMaximumStockModuleView module, ItemStack stack, int quantity)
	{
		return new MaximumStockUpdateMessage(module, stack, true, quantity);
	}

	public static MaximumStockUpdateMessage remove(IMaximumStockModuleView module, ItemStack stack)
	{
		return new MaximumStockUpdateMessage(module, stack, false, 0);
	}

	private MaximumStockUpdateMessage(IMaximumStockModuleView module, ItemStack stack, boolean add, int quantity)
	{
		super(module);

		this.stack = stack.copy();
		this.add = add;
		this.quantity = quantity;
	}

	public MaximumStockUpdateMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.stack = buffer.readItem();
		this.add = buffer.readBoolean();
		this.quantity = buffer.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeItem(this.stack);
		buffer.writeBoolean(this.add);
		buffer.writeInt(this.quantity);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		super.handle(context);

		if (this.getModulePos().getModule() instanceof IMaximumStockModule module)
		{
			if (this.add)
			{
				module.addMaximumStock(this.stack, this.quantity);
			}
			else
			{
				module.removeMaximumStock(this.stack);
			}

		}

	}

	public ItemStack getStack()
	{
		return this.stack.copy();
	}

	public boolean isAdd()
	{
		return this.add;
	}

	public int getQuantity()
	{
		return this.quantity;
	}

}
