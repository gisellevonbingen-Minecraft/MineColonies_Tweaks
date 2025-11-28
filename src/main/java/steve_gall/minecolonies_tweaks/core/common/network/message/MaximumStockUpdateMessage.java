package steve_gall.minecolonies_tweaks.core.common.network.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.api.common.building.module.IMaximumStockModule;
import steve_gall.minecolonies_tweaks.api.common.building.module.IMaximumStockModuleView;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.item.ItemSerializationHelper;

public class MaximumStockUpdateMessage extends BuildingModuleMessage
{
	public static final CustomPacketPayload.Type<MaximumStockUpdateMessage> TYPE = new CustomPacketPayload.Type<>(MineColoniesTweaks.rl("maximum_stock_update"));

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

	public MaximumStockUpdateMessage(RegistryFriendlyByteBuf buffer)
	{
		super(buffer);

		this.stack = ItemSerializationHelper.deserialize(buffer);
		this.add = buffer.readBoolean();
		this.quantity = buffer.readInt();
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buffer)
	{
		super.encode(buffer);

		ItemSerializationHelper.serialize(buffer, this.stack);
		buffer.writeBoolean(this.add);
		buffer.writeInt(this.quantity);
	}

	@Override
	public void handle(IPayloadContext context)
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

	@Override
	public CustomPacketPayload.Type<MaximumStockUpdateMessage> type()
	{
		return TYPE;
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
