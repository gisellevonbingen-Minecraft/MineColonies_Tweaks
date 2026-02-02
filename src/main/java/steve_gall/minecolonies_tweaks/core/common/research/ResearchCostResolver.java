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
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingUniversity;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.IRequestableObject;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.resolvers.CustomizableRequestableResolver;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingUtils;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksBuildingModules;

public class ResearchCostResolver extends CustomizableRequestableResolver<ResearchCost>
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
		if (building.hasModule(MCTweaksBuildingModules.RESEARCH_COST_RESOLVER))
		{
			var module = building.getModule(MCTweaksBuildingModules.RESEARCH_COST_RESOLVER);
			return module.hasResolver();
		}

		return false;
	}

	public static boolean hasResolver(IBuildingView buildingView)
	{
		if (buildingView.hasModuleView(MCTweaksBuildingModules.RESEARCH_COST_RESOLVER))
		{
			var module = buildingView.getModuleView(MCTweaksBuildingModules.RESEARCH_COST_RESOLVER);
			return module.hasResolver();
		}

		return false;
	}

	public ResearchCostResolver(@NotNull ILocation location, @NotNull IToken<?> token)
	{
		super(location, token);
	}

	@Override
	public boolean canResolveRequest(@NotNull IRequestManager manager, @NotNull IRequester requester, @NotNull IRequestableObject request)
	{
		return requester.getLocation().equals(this.getLocation()) && request instanceof ResearchCost;
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
	public @Nullable List<IToken<?>> attemptResolveRequest(@NotNull IRequestManager manager, @NotNull IRequester requester, @NotNull ResearchCost request)
	{
		var list = new ArrayList<IToken<?>>();

		for (var stack : request.getItems())
		{
			list.add(manager.createRequest(this, new Stack(stack)));
		}

		return list;
	}

	@Override
	public void resolveRequest(@NotNull IRequestManager manager, @NotNull IRequester requester, @NotNull ResearchCost request)
	{
		var uuid = request.getRequester();

		if (uuid != null)
		{
			var colony = manager.getColony();
			var player = colony.getWorld().getServer().getPlayerList().getPlayer(uuid);

			if (player != null)
			{
				var globalTree = IGlobalResearchTree.getInstance();
				var branch = globalTree.getBranchData(request.getBranchId());
				var research = branch != null ? globalTree.getResearch(request.getBranchId(), request.getResearchId()) : null;
				var branchName = research != null ? MutableComponent.create(branch.getName()) : Component.literal(request.getBranchId().toString());
				var researchName = research != null ? MutableComponent.create(research.getName()) : Component.literal(request.getResearchId().toString());
				player.sendSystemMessage(Component.translatable("minecolonies_tweaks.text.research_cost.delivery_completed", branchName, researchName).withStyle(ChatFormatting.GRAY));

				if (colony.getServerBuildingManager().getBuilding(this.getLocation().getInDimensionLocation()) instanceof BuildingUniversity building)
				{
					var localTree = colony.getResearchManager().getResearchTree();
					localTree.attemptBeginResearch(player, colony, building, research);
				}

			}

		}

	}

	@Override
	public @NotNull MutableComponent getRequesterDisplayName(@NotNull IRequestManager manager, @NotNull IRequest<?> request)
	{
		if (request.getRequest() instanceof CustomizableRequestable custom && custom.getObject() instanceof ResearchCost)
		{
			var requester = manager.getColony().getRequesterBuildingForPosition(this.getLocation().getInDimensionLocation());
			var displayName = BuildingUtils.getDisplayName(requester);

			if (displayName != null)
			{
				return displayName;
			}

		}

		return super.getRequesterDisplayName(manager, request);
	}

}
