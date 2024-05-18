package steve_gall.minecolonies_tweaks.core.common.building;

import com.minecolonies.api.colony.buildings.modules.ICraftingBuildingModule;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.core.colony.buildings.AbstractBuilding;

public class BuildingHelper
{
	public static ICraftingBuildingModule getCraftableModule(AbstractBuilding building, IToken<?> recipeToken, JobEntry jobEntry)
	{
		for (var module : building.getModulesByType(ICraftingBuildingModule.class))
		{
			if (module.holdsRecipe(recipeToken))
			{
				var job = module.getCraftingJob();

				if (job != null && job.getJobRegistryEntry() == jobEntry)
				{
					return module;
				}

			}

		}

		return null;
	}

	private BuildingHelper()
	{

	}

}
