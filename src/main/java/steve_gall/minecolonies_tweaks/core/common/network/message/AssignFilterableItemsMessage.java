package steve_gall.minecolonies_tweaks.core.common.network.message;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.buildings.modules.IItemListModuleView;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.core.colony.buildings.modules.ItemListModule;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.core.common.util.SerializationHelper;

public class AssignFilterableItemsMessage extends BuildingModuleMessage
{
	public enum Function
	{
		CLEAR,
		ADD,
		REMOVE,
	}

	private final Function function;
	private final List<ItemStorage> storages;

	public AssignFilterableItemsMessage(IItemListModuleView module, Function function, Iterable<ItemStorage> storages)
	{
		super(module);

		this.function = function;
		this.storages = ImmutableList.copyOf(storages);
	}

	public AssignFilterableItemsMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.function = buffer.readEnum(Function.class);
		this.storages = buffer.readList(SerializationHelper.deserializer());
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeEnum(this.function);
		buffer.writeCollection(this.storages, SerializationHelper.serializer());
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		super.handle(context);

		if (this.getModulePos().getModule() instanceof ItemListModule module)
		{
			if (this.function == Function.ADD)
			{
				this.storages.forEach(module::addItem);
			}
			else if (this.function == Function.REMOVE)
			{
				this.storages.forEach(module::removeItem);
			}

		}

	}

	public Function getFunction()
	{
		return this.function;
	}

	public List<ItemStorage> getStorages()
	{
		return this.storages;
	}

}
