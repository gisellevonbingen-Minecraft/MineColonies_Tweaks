package steve_gall.minecolonies_tweaks.core.common.research;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.requestable.Stack;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.core.colony.buildings.AbstractBuilding;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.IRequestableObject;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.resolvers.CustomizableRequestableBuildingResolver;

public class ResearchCostResolver extends CustomizableRequestableBuildingResolver<ResearchCost>
{
	public static ResearchCostResolver deserialize(ILocation location, IToken<?> token, CompoundTag compound)
	{
		return new ResearchCostResolver(location, token);
	}

	public static void serialize(ResearchCostResolver resolver, CompoundTag compound)
	{

	}

	public static boolean hasResolver(IBuilding building)
	{
		for (var resolver : building.getResolvers())
		{
			if (resolver instanceof ResearchCostResolver)
			{
				return true;
			}

		}

		return false;

	}

	public static boolean hasResolver(IBuildingView buildingView)
	{
		for (var token : buildingView.getResolverIds())
		{
			var resolver = buildingView.getColony().getRequestManager().getResolverForToken(token);

			if (resolver instanceof ResearchCostResolver)
			{
				return true;
			}

		}

		return false;

	}

	public ResearchCostResolver(@NotNull ILocation location, @NotNull IToken<?> token)
	{
		super(location, token);
	}

	@Override
	public boolean canResolveForBuilding(@NotNull IRequestManager manager, @NotNull IRequestableObject request, @NotNull AbstractBuilding building)
	{
		return request instanceof ResearchCost;
	}

	@Override
	public void onRequestedRequestComplete(@NotNull IRequestManager manager, @NotNull IRequest<?> request)
	{

	}

	@Override
	public void onRequestedRequestCancelled(@NotNull IRequestManager manager, @NotNull IRequest<?> request)
	{

	}

	@Override
	public void onAssignedRequestBeingCancelled(@NotNull IRequestManager manager, @NotNull ResearchCost request)
	{

	}

	@Override
	public void onAssignedRequestCancelled(@NotNull IRequestManager manager, @NotNull ResearchCost request)
	{

	}

	@Override
	public @Nullable List<IToken<?>> attemptResolveForBuilding(@NotNull IRequestManager manager, @NotNull ResearchCost request, @NotNull AbstractBuilding building)
	{
		var list = new ArrayList<IToken<?>>();

		for (var stack : request.getStacks())
		{
			list.add(manager.createRequest(this, new Stack(stack)));
		}

		return list;
	}

	@Override
	public void resolveForBuilding(@NotNull IRequestManager manager, @NotNull ResearchCost request, @NotNull AbstractBuilding building)
	{
		var uuid = request.getRequester();

		if (uuid != null)
		{
			var player = building.getColony().getWorld().getServer().getPlayerList().getPlayer(uuid);

			if (player != null)
			{
				var tree = IGlobalResearchTree.getInstance();
				var branch = tree.getBranchData(request.getBranchId());
				var research = branch != null ? tree.getResearch(request.getBranchId(), request.getResearchId()) : null;
				var branchName = research != null ? MutableComponent.create(branch.getName()) : Component.literal(request.getBranchId().toString());
				var researchName = research != null ? MutableComponent.create(research.getName()) : Component.literal(request.getResearchId().toString());
				player.sendSystemMessage(Component.translatable("minecolonies_tweaks.text.research_cost.delivery_completed", branchName, researchName).withStyle(ChatFormatting.GRAY));
			}

		}

	}

}
