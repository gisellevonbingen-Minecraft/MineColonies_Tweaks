package steve_gall.minecolonies_tweaks.core.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;

import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;

public class CustomizableRequestableRequest extends CustomizableRequest<CustomizableRequestable>
{
	public CustomizableRequestableRequest(@NotNull IRequester requester, @NotNull IToken<?> token, @NotNull CustomizableRequestable requested)
	{
		super(requester, token, requested);
	}

	public CustomizableRequestableRequest(@NotNull IRequester requester, @NotNull IToken<?> token, @NotNull RequestState state, @NotNull CustomizableRequestable requested)
	{
		super(requester, token, state, requested);
	}

}
