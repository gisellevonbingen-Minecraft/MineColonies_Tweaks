package steve_gall.minecolonies_tweaks.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.api.util.constant.CitizenConstants;
import com.minecolonies.core.entity.ai.workers.AbstractEntityAIStructure;

import steve_gall.minecolonies_tweaks.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = AbstractEntityAIStructure.class, remap = false)
public class AbstractEntityAIStructureMixin
{
	@ModifyConstant(method = "structureStep", constant = @Constant(intValue = 150))
	private int getBuildBlockDelay0(int BUILD_BLOCK_DELAY)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.commonBlockBuildingDelay.get() * CitizenConstants.PROGRESS_MULTIPLIER;
	}

}
