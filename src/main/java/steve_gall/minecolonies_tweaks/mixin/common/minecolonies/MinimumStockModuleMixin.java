package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.research.util.ResearchConstants;
import com.minecolonies.core.colony.buildings.modules.MinimumStockModule;
import com.minecolonies.core.colony.buildings.workerbuildings.PostBox;

@Mixin(value = MinimumStockModule.class, remap = false)
public abstract class MinimumStockModuleMixin
{
	@WrapOperation(method = "minimumStockSize", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/colony/buildings/IBuilding;getBuildingLevel()I", remap = false))
	private int minimumStockSize(IBuilding building, Operation<Integer> operation)
	{
		if (building instanceof PostBox)
		{
			var increase = 1.0D + building.getColony().getResearchManager().getResearchEffects().getEffectStrength(ResearchConstants.MINIMUM_STOCK);
			var townhall = building.getColony().getBuildingManager().getTownHall();
			return townhall == null ? 0 : (int) (townhall.getBuildingLevel() * increase);
		}
		else
		{
			return operation.call(building);
		}

	}

}
