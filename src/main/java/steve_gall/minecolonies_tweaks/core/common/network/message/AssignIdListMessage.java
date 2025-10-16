package steve_gall.minecolonies_tweaks.core.common.network.message;

import java.util.Collection;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.api.common.building.module.IIdListModule;
import steve_gall.minecolonies_tweaks.api.common.building.module.IIdListModuleView;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class AssignIdListMessage extends BuildingModuleMessage
{
	public static final CustomPacketPayload.Type<AssignIdListMessage> TYPE = new CustomPacketPayload.Type<>(MineColoniesTweaks.rl("assign_id_list"));

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

	public AssignIdListMessage(RegistryFriendlyByteBuf buffer)
	{
		super(buffer);

		this.function = buffer.readEnum(Function.class);
		this.ids = buffer.readList(FriendlyByteBuf::readResourceLocation);
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeEnum(this.function);
		buffer.writeCollection(this.ids, FriendlyByteBuf::writeResourceLocation);
	}

	@Override
	public void handle(IPayloadContext context)
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

	@Override
	public CustomPacketPayload.Type<AssignIdListMessage> type()
	{
		return TYPE;
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
