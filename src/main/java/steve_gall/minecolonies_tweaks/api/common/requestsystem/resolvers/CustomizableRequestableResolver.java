package steve_gall.minecolonies_tweaks.api.common.requestsystem.resolvers;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.core.colony.requestsystem.resolvers.core.AbstractRequestResolver;

import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.IRequestableObject;

public abstract class CustomizableRequestableResolver<REQUESTABLE extends IRequestableObject> extends AbstractRequestResolver<CustomizableRequestable> implements ICustomizableRequestResolver<REQUESTABLE>
{
	public CustomizableRequestableResolver(@NotNull ILocation location, @NotNull IToken<?> token)
	{
		super(location, token);
	}

	public abstract boolean canResolveRequest(@NotNull IRequestManager manager, @NotNull IRequester requester, @NotNull IRequestableObject request);

	@Nullable
	public abstract List<IToken<?>> attemptResolveRequest(@NotNull IRequestManager manager, @NotNull IRequester requester, @NotNull REQUESTABLE request);

	public abstract void resolveRequest(@NotNull IRequestManager manager, @NotNull IRequester requester, @NotNull REQUESTABLE request);

	@Override
	public boolean canResolveRequest(@NotNull IRequestManager manager, @NotNull IRequest<? extends CustomizableRequestable> request)
	{
		return this.canResolveRequest(manager, request.getRequester(), request.getRequest().getObject());
	}

	@Override
	public @Nullable List<IToken<?>> attemptResolveRequest(@NotNull IRequestManager manager, @NotNull IRequest<? extends CustomizableRequestable> request)
	{
		return this.attemptResolveRequest(manager, request.getRequester(), this.getRequestableObject(request));
	}

	@Override
	public void resolveRequest(@NotNull IRequestManager manager, @NotNull IRequest<? extends CustomizableRequestable> request)
	{
		this.resolveRequest(manager, request.getRequester(), this.getRequestableObject(request));
		manager.updateRequestState(request.getId(), RequestState.RESOLVED);
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

}
