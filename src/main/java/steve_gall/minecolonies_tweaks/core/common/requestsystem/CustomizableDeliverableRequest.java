package steve_gall.minecolonies_tweaks.core.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;

import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableDeliverable;

public class CustomizableDeliverableRequest extends CustomizableRequest<CustomizableDeliverable>
{
	public CustomizableDeliverableRequest(@NotNull IRequester requester, @NotNull IToken<?> token, @NotNull CustomizableDeliverable requested)
	{
		super(requester, token, requested);
	}

	public CustomizableDeliverableRequest(@NotNull IRequester requester, @NotNull IToken<?> token, @NotNull RequestState state, @NotNull CustomizableDeliverable requested)
	{
		super(requester, token, state, requested);
	}

}
