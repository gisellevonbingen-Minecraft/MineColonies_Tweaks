package steve_gall.minecolonies_tweaks.core.common.network.message;

import java.util.Collection;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import steve_gall.minecolonies_tweaks.api.common.building.module.IIdListModule;
import steve_gall.minecolonies_tweaks.api.common.building.module.IIdListModuleView;

public class AssignIdListMessage extends BuildingModuleMessage
{
	public enum Function
	{
		CLEAR,
		ADD,
		REMOVE,
	}

	private final Function function;
	private final Collection<ResourceLocation> ids;

	public AssignIdListMessage(IIdListModuleView module, Function function, Collection<ResourceLocation> ids)
	{
		super(module);

		this.function = function;
		this.ids = ids.stream().toList();
	}

	public AssignIdListMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.function = buffer.readEnum(Function.class);
		this.ids = buffer.readList(FriendlyByteBuf::readResourceLocation);
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeEnum(this.function);
		buffer.writeCollection(this.ids, FriendlyByteBuf::writeResourceLocation);
	}

	@Override
	public void handle(Context context)
	{
		super.handle(context);

		if (this.getModulePos().getModule() instanceof IIdListModule module)
		{
			if (this.function == Function.CLEAR)
			{
				module.clearIds();
			}
			else if (this.function == Function.ADD)
			{
				module.addIds(this.ids);
			}
			else if (this.function == Function.REMOVE)
			{
				module.removeIds(this.ids);
			}

		}

	}

	public Function getFunction()
	{
		return this.function;
	}

	public Collection<ResourceLocation> getIds()
	{
		return this.ids;
	}

}
