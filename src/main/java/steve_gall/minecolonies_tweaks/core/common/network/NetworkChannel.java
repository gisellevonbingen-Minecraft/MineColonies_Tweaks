package steve_gall.minecolonies_tweaks.core.common.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.network.message.AssignFilterableItemsMessage;

public class NetworkChannel
{
	private final SimpleChannel rawChannel;
	private final AtomicInteger rawMessageId;

	private final AtomicInteger transactionId;
	private final Cache<Integer, Map<Integer, byte[]>> messageCache;
	private final Map<Integer, MessageEntry<?>> idToEntryMap;
	private final Map<Class<?>, MessageEntry<?>> classToIdMap;
	private final AtomicInteger messageId;

	public NetworkChannel(String channelName)
	{
		var modVersion = ModList.get().getModContainerById(MineColoniesTweaks.MOD_ID).get().getModInfo().getVersion().toString();
		this.rawChannel = NetworkRegistry.newSimpleChannel(MineColoniesTweaks.rl(channelName), () -> modVersion, modVersion::equals, modVersion::equals);
		this.rawMessageId = new AtomicInteger();
		this.rawChannel.registerMessage(this.rawMessageId.incrementAndGet(), FrameMessage.class, FrameMessage::encode, FrameMessage::new, (msg, supplier) ->
		{
			var context = supplier.get();
			context.setPacketHandled(true);
			msg.handle(context);
		});

		this.messageId = new AtomicInteger();
		this.messageCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).concurrencyLevel(8).build();
		this.idToEntryMap = new HashMap<>();
		this.classToIdMap = new HashMap<>();
		this.transactionId = new AtomicInteger();

		this.registerMessage(AssignFilterableItemsMessage.class, AssignFilterableItemsMessage::new);
	}

	public void handleSplit(AbstractMessage message, Consumer<FrameMessage> consumer)
	{
		var entry = this.getClassToIdMap().get(message.getClass());

		if (entry == null)
		{
			throw new IllegalArgumentException("Not registered message type: " + message.getClass());
		}

		var buffer = Unpooled.buffer();
		byte[] messageBytes;

		try
		{
			message.encode(new FriendlyByteBuf(buffer));
			messageBytes = buffer.array();
		}
		finally
		{
			buffer.release();
		}

		var max_packet_size = 943718; // This is 90% of max packet size.
		var currentIndex = 0;
		var packetIndex = 0;
		var transactionId = this.transactionId.getAndIncrement();

		while (currentIndex < messageBytes.length)
		{
			var splitLength = Math.min(max_packet_size, messageBytes.length - currentIndex);
			var splitBytes = Arrays.copyOfRange(messageBytes, currentIndex, currentIndex + splitLength);
			var splitMessage = new FrameMessage(transactionId, packetIndex++, (currentIndex + splitLength) >= messageBytes.length, entry.getMessageId(), splitBytes);
			consumer.accept(splitMessage);
			currentIndex += splitLength;
		}

	}

	public void sendToServer(AbstractMessage message)
	{
		this.handleSplit(message, this.rawChannel::sendToServer);
	}

	public void sendToPlayer(AbstractMessage message, ServerPlayer player)
	{
		this.handleSplit(message, split -> this.rawChannel.sendTo(split, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
	}

	public <MSG extends AbstractMessage> void registerMessage(Class<MSG> messageClass, Function<FriendlyByteBuf, MSG> decoder)
	{
		var id = this.messageId.incrementAndGet();
		var entry = new MessageEntry<>(id, messageClass, decoder);
		this.getIdToEntryMap().put(id, entry);
		this.getClassToIdMap().put(messageClass, entry);
	}

	public Cache<Integer, Map<Integer, byte[]>> getMessageCache()
	{
		return this.messageCache;
	}

	public Map<Integer, MessageEntry<?>> getIdToEntryMap()
	{
		return this.idToEntryMap;
	}

	public Map<Class<?>, MessageEntry<?>> getClassToIdMap()
	{
		return this.classToIdMap;
	}

}
