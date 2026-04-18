package steve_gall.minecolonies_tweaks.core.common.network.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.item.ItemCopyScroll;

public class CopyScrollRemoveEntryMessage extends AbstractMessage
{
	public static final CustomPacketPayload.Type<CopyScrollRemoveEntryMessage> TYPE = new CustomPacketPayload.Type<>(MineColoniesTweaks.rl("copyscroll_remove_entry"));

	private final String key;
	private final InteractionHand hand;

	public CopyScrollRemoveEntryMessage(String key, InteractionHand hand)
	{
		this.key = key;
		this.hand = hand;
	}

	public CopyScrollRemoveEntryMessage(RegistryFriendlyByteBuf buffer)
	{
		super(buffer);

		this.key = buffer.readUtf();
		this.hand = buffer.readEnum(InteractionHand.class);
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeUtf(this.key);
		buffer.writeEnum(this.hand);
	}

	@Override
	public Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context)
	{
		super.handle(context);

		var player = context.player();
		var stack = player.getItemInHand(this.hand);

		if (stack.getItem() instanceof ItemCopyScroll copyScroll)
		{
			copyScroll.removeEntry(stack, this.key);
		}

	}

	public String getKey()
	{
		return this.key;
	}

	public InteractionHand getHand()
	{
		return this.hand;
	}

}
