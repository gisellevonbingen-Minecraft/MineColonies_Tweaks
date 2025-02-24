package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.colony.buildings.modules.AbstractAssignedCitizenModule;
import com.minecolonies.core.colony.buildings.modules.CourierAssignmentModule;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = CourierAssignmentModule.class, remap = false)
public abstract class CourierAssignmentModuleMixin extends AbstractAssignedCitizenModule
{
	@ModifyConstant(method = "getModuleMax", remap = false, constant = @Constant(intValue = 2))
	private int getModuleMax(int levelperCouriers)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.warehouseLevelPerCouriers.get();
	}

}
