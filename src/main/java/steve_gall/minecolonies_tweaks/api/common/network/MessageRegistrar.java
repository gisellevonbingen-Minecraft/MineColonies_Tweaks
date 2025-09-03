package steve_gall.minecolonies_tweaks.api.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import steve_gall.minecolonies_tweaks.core.common.util.SerializationHelper.WrappedStreamDecoder;

public class MessageRegistrar
{
	private final PayloadRegistrar registrar;

	public MessageRegistrar(PayloadRegistrar registrar)
	{
		this.registrar = registrar;
	}

	public <MESSAGE extends AbstractMessage> void playBidirectional(CustomPacketPayload.Type<MESSAGE> type, WrappedStreamDecoder<MESSAGE> decoder)
	{
		this.registrar.playBidirectional(type, this.codec(decoder), MESSAGE::handle);
	}

	public <MESSAGE extends AbstractMessage> void playToClient(CustomPacketPayload.Type<MESSAGE> type, WrappedStreamDecoder<MESSAGE> decoder)
	{
		this.registrar.playToClient(type, this.codec(decoder), MESSAGE::handle);
	}

	public <MESSAGE extends AbstractMessage> void playToServer(CustomPacketPayload.Type<MESSAGE> type, WrappedStreamDecoder<MESSAGE> decoder)
	{
		this.registrar.playToServer(type, this.codec(decoder), MESSAGE::handle);
	}

	public <MESSAGE extends AbstractMessage> StreamCodec<RegistryFriendlyByteBuf, MESSAGE> codec(WrappedStreamDecoder<MESSAGE> decoder)
	{
		return new StreamCodec<>()
		{
			@Override
			public MESSAGE decode(RegistryFriendlyByteBuf buffer)
			{
				return decoder.apply(buffer);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buffer, MESSAGE message)
			{
				message.encode(buffer);
			}
		};

	}

}
