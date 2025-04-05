package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.core.colony.buildings.modules.MinimumStockModule;
import com.minecolonies.core.colony.buildings.workerbuildings.PostBox;

@Mixin(value = MinimumStockModule.class, remap = false)
public abstract class MinimumStockModuleMixin
{
	@Redirect(method = "minimumStockSize", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/colony/buildings/IBuilding;getBuildingLevel()I", remap = false))
	private int minimumStockSize(IBuilding building)
	{
		if (building instanceof PostBox)
		{
			var townhall = building.getColony().getBuildingManager().getTownHall();
			return townhall == null ? 0 : townhall.getBuildingLevel();
		}
		else
		{
			return building.getBuildingLevel();
		}

	}

}
