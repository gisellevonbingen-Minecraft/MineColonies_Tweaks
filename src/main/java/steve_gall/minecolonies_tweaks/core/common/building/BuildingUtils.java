package steve_gall.minecolonies_tweaks.core.common.building;

import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BuildingUtils
{
	public static boolean isUnlocked(IColony colony, BuildingEntry building, int level)
	{
		var hutResearch = colony.getResearchManager().getResearchEffectIdFrom(building.getBuildingBlock());

		if (MinecoloniesAPIProxy.getInstance().getGlobalResearchTree().hasResearchEffect(hutResearch))
		{
			if (colony.getResearchManager().getResearchEffects().getEffectStrength(hutResearch) < Math.max(1, level))
			{
				return false;
			}

		}

		return true;
	}

	private static MutableComponent getDisplayName(BuildingEntry type, String customName, int level)
	{
		var buildingName = Component.empty();
		buildingName.append(customName.isEmpty() ? Component.translatable(type.getBuildingBlock().getDescriptionId()) : Component.literal(customName));

		if (hasLevel(type))
		{
			buildingName.append(" ").append(String.valueOf(level));
		}

		return buildingName;
	}

	public static boolean hasLevel(BuildingEntry type)
	{
		return type != ModBuildings.stash.get() && type != ModBuildings.postBox.get();
	}

	@Nullable
	public static MutableComponent getDisplayName(IRequester requester)
	{
		if (requester instanceof IBuilding building)
		{
			return getDisplayName(building);
		}
		else if (requester instanceof IBuildingView buildingView)
		{
			return getDisplayName(buildingView);
		}
		else
		{
			return null;
		}

	}

	public static MutableComponent getDisplayName(IBuilding building)
	{
		return getDisplayName(building.getBuildingType(), building.getCustomName(), building.getBuildingLevel());
	}

	public static MutableComponent getDisplayName(IBuildingView buildingView)
	{
		return getDisplayName(buildingView.getBuildingType(), buildingView.getCustomName(), buildingView.getBuildingLevel());
	}

	private BuildingUtils()
	{

	}

}
