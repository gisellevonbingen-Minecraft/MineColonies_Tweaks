package steve_gall.minecolonies_tweaks.core.common.init;

import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequests.ItemStackListRequest;

import net.minecraft.network.chat.Component;
import steve_gall.minecolonies_tweaks.core.common.building.module.MaximumStockModule;
import steve_gall.minecolonies_tweaks.core.common.building.module.ResearchCostResolverBuildingModule;
import steve_gall.minecolonies_tweaks.core.common.building.module.StudyItemListModule;

public class MCTweaksBuildingModules
{
	public static final BuildingEntry.ModuleProducer<ResearchCostResolverBuildingModule, ResearchCostResolverBuildingModule.View> RESEARCH_COST_RESOLVER = new BuildingEntry.ModuleProducer<>("research_cost_resolver", //
			() -> new ResearchCostResolverBuildingModule(), //
			() -> ResearchCostResolverBuildingModule.View::new);

	public static final BuildingEntry.ModuleProducer<MaximumStockModule, MaximumStockModule.View> MAXIMUM_STOCK = new BuildingEntry.ModuleProducer<>("maximum_stock", //
			() -> new MaximumStockModule(), //
			() -> MaximumStockModule.View::new);

	public static final BuildingEntry.ModuleProducer<StudyItemListModule, StudyItemListModule.View> STUDY_ITEM_BLACKLIST = new BuildingEntry.ModuleProducer<>("study_item_blacklist", //
			() -> new StudyItemListModule("study_item_blacklist")
			{
				@Override
				protected void onIdsChanged()
				{
					super.onIdsChanged();

					for (var citizenData : this.building.getAllAssignedCitizen())
					{
						for (var request : this.building.getOpenRequests(citizenData.getId()))
						{
							if (request instanceof ItemStackListRequest itemStackListRequest)
							{
								if (itemStackListRequest.getRequest().getDescription().equals("Study Items"))
								{
									this.building.getColony().getRequestManager().updateRequestState(request.getId(), RequestState.CANCELLED);
								}

							}

						}

					}

				}
			}, //
			() -> () -> new StudyItemListModule.View("study_item_blacklist", Component.translatable("com.minecolonies.coremod.gui.workerhuts.study_item_blacklist"), true));

	public static void init()
	{

	}

	private MCTweaksBuildingModules()
	{

	}

}
