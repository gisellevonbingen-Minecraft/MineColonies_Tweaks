package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.api.util.constant.CitizenConstants;
import com.minecolonies.core.entity.ai.workers.crafting.AbstractEntityAICrafting;

import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = AbstractEntityAICrafting.class, remap = false)
public abstract class AbstractEntityAICraftingMixin
{
	@ModifyConstant(method = "idle", remap = false, constant = @Constant(intValue = 400))
	private int idle_setDelay(int timeout)
	{
		if (timeout == CitizenConstants.TICKS_20 * 20)
		{
			return MCTweaksConfigServer.INSTANCE.jobs.craftingDecideDelay.get();
		}
		else
		{
			return timeout;
		}

	}

	@ModifyConstant(method = "getRequiredProgressForMakingRawMaterial", remap = false, constant = @Constant(intValue = 10))
	private int modifyProgressMuliplier(int PROGRESS_MULTIPLIER)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.craftingProgressMultiplier.get();
	}

	@ModifyConstant(method = "getRequiredProgressForMakingRawMaterial", remap = false, constant = @Constant(intValue = 3))
	private int modifyHittingTime(int HITTING_TIME)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.craftingHittingTime.get();
	}

}
