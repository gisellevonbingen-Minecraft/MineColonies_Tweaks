package steve_gall.minecolonies_tweaks.api.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public abstract class AbstractMessage implements CustomPacketPayload
{
	protected AbstractMessage()
	{

	}

	protected AbstractMessage(RegistryFriendlyByteBuf buffer)
	{

	}

	public void encode(RegistryFriendlyByteBuf buffer)
	{

	}

	public void handle(IPayloadContext context)
	{

	}

}
