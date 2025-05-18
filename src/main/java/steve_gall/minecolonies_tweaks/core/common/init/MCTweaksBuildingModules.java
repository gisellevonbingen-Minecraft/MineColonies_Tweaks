package steve_gall.minecolonies_tweaks.core.common.init;

import com.minecolonies.api.colony.buildings.registry.BuildingEntry;

import steve_gall.minecolonies_tweaks.core.common.building.module.ResearchCostResolverBuildingModule;

public class MCTweaksBuildingModules
{
	public static final BuildingEntry.ModuleProducer<ResearchCostResolverBuildingModule, ResearchCostResolverBuildingModule.View> RESEARCH_COST_RESOLVER = new BuildingEntry.ModuleProducer<>("research_cost_resolver", //
			() -> new ResearchCostResolverBuildingModule(), //
			() -> ResearchCostResolverBuildingModule.View::new);

	private MCTweaksBuildingModules()
	{

	}

}
