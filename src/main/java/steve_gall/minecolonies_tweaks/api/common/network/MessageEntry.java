package steve_gall.minecolonies_tweaks.api.common.network;

import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;

public class MessageEntry<MSG extends AbstractMessage>
{
	private final int messageId;
	private final Class<MSG> messageClass;
	private final Function<FriendlyByteBuf, MSG> decoder;

	public MessageEntry(int messageId, Class<MSG> messageClass, Function<FriendlyByteBuf, MSG> decoder)
	{
		this.messageId = messageId;
		this.messageClass = messageClass;
		this.decoder = decoder;
	}

	public int getMessageId()
	{
		return messageId;
	}

	public Class<MSG> getMessageClass()
	{
		return this.messageClass;
	}

	public Function<FriendlyByteBuf, MSG> getDecoder()
	{
		return this.decoder;
	}

}
