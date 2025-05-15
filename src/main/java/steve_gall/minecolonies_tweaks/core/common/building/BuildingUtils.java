package steve_gall.minecolonies_tweaks.core.common.building;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.views.IBuildingView;

import net.minecraft.network.chat.Component;

public class BuildingUtils
{
	private static Component getDisplayName(BuildingEntry type, String customName, int level)
	{
		var buildingName = Component.empty();
		buildingName.append(customName.isEmpty() ? Component.translatable(type.getTranslationKey()) : Component.literal(customName));
		buildingName.append(" ").append(String.valueOf(level));
		return buildingName;
	}

	public static Component getDisplayName(IBuilding building)
	{
		return getDisplayName(building.getBuildingType(), building.getCustomName(), building.getBuildingLevel());
	}

	public static Component getDisplayName(IBuildingView buildingView)
	{
		return getDisplayName(buildingView.getBuildingType(), buildingView.getCustomName(), buildingView.getBuildingLevel());
	}

	private BuildingUtils()
	{

	}

}
