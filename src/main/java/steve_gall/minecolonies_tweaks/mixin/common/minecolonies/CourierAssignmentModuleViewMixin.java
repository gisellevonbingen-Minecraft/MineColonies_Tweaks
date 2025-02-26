package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.core.colony.buildings.moduleviews.CourierAssignmentModuleView;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = CourierAssignmentModuleView.class, remap = false)
public abstract class CourierAssignmentModuleViewMixin extends AbstractBuildingModuleView
{
	@ModifyConstant(method = "getMaxInhabitants", remap = false, constant = @Constant(intValue = 2))
	private int getMaxInhabitants(int levelperCouriers)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.warehouseLevelPerCouriers.get();
	}

}
