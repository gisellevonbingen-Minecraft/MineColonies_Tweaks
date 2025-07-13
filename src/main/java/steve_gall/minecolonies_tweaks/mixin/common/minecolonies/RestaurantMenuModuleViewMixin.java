package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.colony.buildings.modules.RestaurantMenuModule;
import com.minecolonies.core.colony.buildings.moduleviews.RestaurantMenuModuleView;

import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = RestaurantMenuModuleView.class, remap = false)
public abstract class RestaurantMenuModuleViewMixin
{
	@ModifyConstant(method = "hasReachedLimit", remap = false, constant = @Constant(intValue = RestaurantMenuModule.STOCK_PER_LEVEL))
	private int hasReachedLimit(int stack_per_level)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.menuPerLevel.get();
	}

}
