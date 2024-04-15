package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.requestable.IRequestable;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IObjectConstructor;

public class RequestFactoryHelper
{
	@NotNull
	public static <REQUESTABLE extends IRequestable, REQUEST extends IRequest<REQUESTABLE>> IObjectConstructor<REQUESTABLE, REQUEST> getObjectConstructor(@NotNull IFactoryController controller, @NotNull Class<? extends REQUEST> outputClass)
	{
		return getObjectConstructor(controller, TypeToken.of(outputClass));
	}

	@NotNull
	public static <REQUESTABLE extends IRequestable, REQUEST extends IRequest<REQUESTABLE>> IObjectConstructor<REQUESTABLE, REQUEST> getObjectConstructor(@NotNull IFactoryController controller, @NotNull TypeToken<? extends REQUEST> outputToken)
	{
		return (requested, token, requester, requestState) -> controller.getNewInstance(outputToken, requested, token, requester, requestState);
	}

	private RequestFactoryHelper()
	{

	}

}
