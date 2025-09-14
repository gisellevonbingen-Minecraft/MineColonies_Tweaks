package steve_gall.minecolonies_tweaks.api.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public abstract class AbstractMessage
{
	protected AbstractMessage()
	{

	}

	protected AbstractMessage(FriendlyByteBuf buffer)
	{

	}

	public void encode(FriendlyByteBuf buffer)
	{

	}

	public void handle(NetworkEvent.Context context)
	{

	}

}
