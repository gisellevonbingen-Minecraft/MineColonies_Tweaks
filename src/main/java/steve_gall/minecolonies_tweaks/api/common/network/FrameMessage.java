package steve_gall.minecolonies_tweaks.api.common.network;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.google.common.collect.Maps;
import com.google.common.primitives.Bytes;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public final class FrameMessage
{
	public static final Comparator<Entry<Integer, byte[]>> COMPARING_BY_KEY = Map.Entry.comparingByKey();
	public static final byte[] REDUCE_SEED = new byte[0];

	private final NetworkChannel networkChannel;

	private int transactionId;
	private int splitIndex;
	private boolean isLast;

	private int messageId;
	private byte[] payload;

	public FrameMessage(NetworkChannel networkChannel, int transactionId, int splitIndex, boolean isLast, int messageId, byte[] payload)
	{
		this.networkChannel = networkChannel;

		this.transactionId = transactionId;
		this.splitIndex = splitIndex;
		this.isLast = isLast;

		this.messageId = messageId;
		this.payload = payload;
	}

	public FrameMessage(NetworkChannel networkChannel, FriendlyByteBuf buffer)
	{
		this.networkChannel = networkChannel;

		this.transactionId = buffer.readVarInt();
		this.splitIndex = buffer.readVarInt();
		this.isLast = buffer.readBoolean();

		this.messageId = buffer.readVarInt();
		this.payload = buffer.readByteArray();
	}

	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeVarInt(this.transactionId);
		buffer.writeVarInt(this.splitIndex);
		buffer.writeBoolean(this.isLast);

		buffer.writeVarInt(this.messageId);
		buffer.writeByteArray(this.payload);
	}

	public void handle(NetworkEvent.Context context)
	{
		try
		{
			var cache = this.networkChannel.getMessageCache();

			Map<Integer, byte[]> map;

			synchronized (cache)
			{
				map = cache.get(this.transactionId, Maps::newConcurrentMap);
				map.put(this.splitIndex, this.payload);
			}

			if (!this.isLast)
			{
				return;
			}

			var messageData = map.entrySet().stream().sorted(COMPARING_BY_KEY).map(Map.Entry::getValue).reduce(REDUCE_SEED, Bytes::concat);
			var messageEntry = this.networkChannel.getIdToEntryMap().get(this.messageId);

			if (messageEntry == null)
			{
				MineColoniesTweaks.LOGGER.error("Not registered message id: " + this.networkChannel.getModId() + "." + this.networkChannel.getName() + "." + this.messageId);
				return;
			}

			var buffer = Unpooled.wrappedBuffer(messageData);
			AbstractMessage message;

			try
			{
				message = messageEntry.getDecoder().apply(new FriendlyByteBuf(buffer));
			}
			finally
			{
				buffer.release();
			}

			context.enqueueWork(() ->
			{
				try
				{
					message.handle(context);
				}
				catch (Exception e)
				{
					MineColoniesTweaks.LOGGER.error(e);
				}
			});

		}
		catch (ExecutionException e)
		{
			MineColoniesTweaks.LOGGER.error(e);
		}

	}

}
