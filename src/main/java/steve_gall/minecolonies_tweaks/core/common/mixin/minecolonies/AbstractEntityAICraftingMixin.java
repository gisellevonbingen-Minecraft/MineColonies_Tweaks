package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.entity.ai.basic.AbstractEntityAICrafting;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = AbstractEntityAICrafting.class, remap = false)
public abstract class AbstractEntityAICraftingMixin
{
	@ModifyConstant(method = "getRequiredProgressForMakingRawMaterial", remap = false, constant = @Constant(intValue = 10))
	private int modifyProgressMuliplier(int PROGRESS_MULTIPLIER)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.craftingProgressMultiplier.get();
	}

	@ModifyConstant(method = "getRequiredProgressForMakingRawMaterial", remap = false, constant = @Constant(intValue = 3))
	private int modifyHittingTime(int HITTING_TIME)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.craftingHittingTime.get();
	}

}
