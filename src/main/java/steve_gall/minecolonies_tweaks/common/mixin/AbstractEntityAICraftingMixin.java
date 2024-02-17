package steve_gall.minecolonies_tweaks.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.entity.ai.basic.AbstractEntityAICrafting;

import steve_gall.minecolonies_tweaks.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = AbstractEntityAICrafting.class, remap = false)
public class AbstractEntityAICraftingMixin
{
	// private int getRequiredProgressForMakingRawMaterial()
	// {
	// final int jobModifier = worker.getCitizenData().getCitizenSkillHandler().getLevel(((CraftingWorkerBuildingModule) getModuleForJob()).getCraftSpeedSkill()) / 2;
	// return PROGRESS_MULTIPLIER / Math.min(jobModifier + 1, MAX_LEVEL) * HITTING_TIME;
	// }

	@ModifyConstant(method = "getRequiredProgressForMakingRawMaterial", constant = @Constant(intValue = 10))
	private int modifyProgressMuliplier(int PROGRESS_MULTIPLIER)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.craftingProgressMultiplier.get();
	}

	@ModifyConstant(method = "getRequiredProgressForMakingRawMaterial", constant = @Constant(intValue = 3))
	private int modifyHittingTime(int HITTING_TIME)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.craftingHittingTime.get();
	}

	// @Inject(method = "getRequiredProgressForMakingRawMaterial", at = @At(value = "RETURN"))
	// private void getRequiredProgressForMakingRawMaterial(CallbackInfoReturnable<Integer> cir)
	// {
	// MineColoniesTweaks.LOGGER.info("getRequiredProgressForMakingRawMaterial: " + cir.getReturnValueI());
	// }

}
