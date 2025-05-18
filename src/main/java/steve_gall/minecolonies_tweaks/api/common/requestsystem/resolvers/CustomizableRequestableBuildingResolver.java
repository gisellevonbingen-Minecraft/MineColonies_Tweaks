package steve_gall.minecolonies_tweaks.api.common.requestsystem.resolvers;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.requestsystem.resolvers.core.AbstractBuildingDependentRequestResolver;

import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.IRequestableObject;

public abstract class CustomizableRequestableBuildingResolver<REQUESTABLE extends IRequestableObject> extends AbstractBuildingDependentRequestResolver<CustomizableRequestable> implements ICustomizableRequestResolver<REQUESTABLE>
{
	public CustomizableRequestableBuildingResolver(@NotNull ILocation location, @NotNull IToken<?> token)
	{
		super(location, token);
	}

	@Override
	public Optional<IRequester> getBuilding(@NotNull IRequestManager manager, @NotNull IToken<?> token)
	{
		if (!manager.getColony().getWorld().isClientSide())
		{
			return Optional.ofNullable(manager.getColony().getRequesterBuildingForPosition(this.getLocation().getInDimensionLocation()));
		}

		return Optional.empty();
	}

	public abstract boolean canResolveForBuilding(@NotNull IRequestManager manager, @NotNull IRequestableObject request, @NotNull AbstractBuilding building);

	@Nullable
	public abstract List<IToken<?>> attemptResolveForBuilding(@NotNull IRequestManager manager, @NotNull REQUESTABLE request, @NotNull AbstractBuilding building);

	public abstract void resolveForBuilding(@NotNull IRequestManager manager, @NotNull REQUESTABLE request, @NotNull AbstractBuilding building);

	@Override
	public boolean canResolveForBuilding(@NotNull IRequestManager manager, @NotNull IRequest<? extends CustomizableRequestable> request, @NotNull AbstractBuilding building)
	{
		return this.canResolveForBuilding(manager, request.getRequest().getObject(), building);
	}

	@Override
	public @Nullable List<IToken<?>> attemptResolveForBuilding(@NotNull IRequestManager manager, @NotNull IRequest<? extends CustomizableRequestable> request, @NotNull AbstractBuilding building)
	{
		return this.attemptResolveForBuilding(manager, this.getRequestableObject(request), building);
	}

	@Override
	public void resolveForBuilding(@NotNull IRequestManager manager, @NotNull IRequest<? extends CustomizableRequestable> request, @NotNull AbstractBuilding building)
	{
		this.resolveForBuilding(manager, this.getRequestableObject(request), building);
		manager.updateRequestState(request.getId(), RequestState.RESOLVED);
	}

}
