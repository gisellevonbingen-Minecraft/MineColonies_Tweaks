package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.entity.ai.workers.crafting.AbstractEntityAICrafting;

import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = AbstractEntityAICrafting.class, remap = false)
public abstract class AbstractEntityAICraftingMixin
{
	@ModifyConstant(method = "decide", remap = false, constant = @Constant(intValue = 400))
	private int decide_setDelay(int timeout)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.craftingDecideDelay.get();
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
