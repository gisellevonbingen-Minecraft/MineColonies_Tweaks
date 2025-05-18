package steve_gall.minecolonies_tweaks.api.common.requestsystem.resolvers;

import org.jetbrains.annotations.NotNull;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.resolver.IRequestResolver;

import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.IRequestableObject;

public interface ICustomizableRequestResolver<REQUESTABLE extends IRequestableObject> extends IRequestResolver<CustomizableRequestable>
{
	void onAssignedRequestBeingCancelled(@NotNull IRequestManager manager, @NotNull REQUESTABLE request);

	void onAssignedRequestCancelled(@NotNull IRequestManager manager, @NotNull REQUESTABLE request);

	@Override
	default TypeToken<? extends CustomizableRequestable> getRequestType()
	{
		return CustomizableRequestable.TYPE_TOKEN;
	}

	@Override
	default void onAssignedRequestBeingCancelled(@NotNull IRequestManager manager, @NotNull IRequest<? extends CustomizableRequestable> request)
	{
		this.onAssignedRequestBeingCancelled(manager, this.getRequestableObject(request));
	}

	@Override
	default void onAssignedRequestCancelled(@NotNull IRequestManager manager, @NotNull IRequest<? extends CustomizableRequestable> request)
	{
		this.onAssignedRequestCancelled(manager, this.getRequestableObject(request));
	}

	@SuppressWarnings("unchecked")
	@NotNull
	default REQUESTABLE getRequestableObject(@NotNull IRequest<?> request)
	{
		return (REQUESTABLE) ((CustomizableRequestable) request.getRequest()).getObject();
	}

}
