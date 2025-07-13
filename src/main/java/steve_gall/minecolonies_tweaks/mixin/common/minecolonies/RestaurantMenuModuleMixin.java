package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.colony.buildings.modules.RestaurantMenuModule;

import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = RestaurantMenuModule.class, remap = false)
public abstract class RestaurantMenuModuleMixin
{
	@ModifyConstant(method = "addMenuItem", remap = false, constant = @Constant(intValue = RestaurantMenuModule.STOCK_PER_LEVEL))
	private int addMenuItem(int stack_per_level)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.menuPerLevel.get();
	}

}
