package steve_gall.minecolonies_tweaks.core.common.requestsystem;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.core.colony.requestsystem.requests.AbstractRequest;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableDeliverable;

public class CustomizableDeliverableRequest extends AbstractRequest<CustomizableDeliverable>
{
	public CustomizableDeliverableRequest(@NotNull IRequester requester, @NotNull IToken<?> token, @NotNull CustomizableDeliverable requested)
	{
		super(requester, token, requested);
	}

	public CustomizableDeliverableRequest(@NotNull IRequester requester, @NotNull IToken<?> token, @NotNull RequestState state, @NotNull CustomizableDeliverable requested)
	{
		super(requester, token, state, requested);
	}

	@NotNull
	@Override
	public Component getShortDisplayString()
	{
		var request = this.getRequest();
		var object = request.getObject();

		if (object != null)
		{
			return object.getShortDisplayString();
		}
		else
		{
			return Component.literal("Unknown request id: " + request.getId());
		}

	}

	@NotNull
	@Override
	public Component getLongDisplayString()
	{
		var request = this.getRequest();
		var object = request.getObject();

		if (object != null)
		{
			return object.getLongDisplayString();
		}
		else
		{
			return Component.literal("Unknown request id: " + request.getId());
		}

	}

	@NotNull
	@Override
	public List<ItemStack> getDisplayStacks()
	{
		var object = this.getRequest().getObject();
		return object != null ? object.getDisplayStacks() : Collections.emptyList();
	}

}
