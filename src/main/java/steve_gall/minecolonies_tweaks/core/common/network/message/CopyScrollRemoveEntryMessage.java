package steve_gall.minecolonies_tweaks.core.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.core.common.item.ItemCopyScroll;

public class CopyScrollRemoveEntryMessage extends AbstractMessage
{
	private final String key;
	private final InteractionHand hand;

	public CopyScrollRemoveEntryMessage(String key, InteractionHand hand)
	{
		this.key = key;
		this.hand = hand;
	}

	public CopyScrollRemoveEntryMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.key = buffer.readUtf();
		this.hand = buffer.readEnum(InteractionHand.class);
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeUtf(this.key);
		buffer.writeEnum(this.hand);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		super.handle(context);

		var player = context.getSender();
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
