package steve_gall.minecolonies_tweaks.core.common.init;

import com.minecolonies.api.colony.buildings.registry.BuildingEntry;

import steve_gall.minecolonies_tweaks.core.common.building.module.MaximumStockModule;
import steve_gall.minecolonies_tweaks.core.common.building.module.ResearchCostResolverBuildingModule;

public class MCTweaksBuildingModules
{
	public static final BuildingEntry.ModuleProducer<ResearchCostResolverBuildingModule, ResearchCostResolverBuildingModule.View> RESEARCH_COST_RESOLVER = new BuildingEntry.ModuleProducer<>("research_cost_resolver", //
			() -> new ResearchCostResolverBuildingModule(), //
			() -> ResearchCostResolverBuildingModule.View::new);

	public static final BuildingEntry.ModuleProducer<MaximumStockModule, MaximumStockModule.View> MAXIMUM_STOCK = new BuildingEntry.ModuleProducer<>("maximum_stock", //
			() -> new MaximumStockModule(), //
			() -> MaximumStockModule.View::new);

	private MCTweaksBuildingModules()
	{

	}

}
