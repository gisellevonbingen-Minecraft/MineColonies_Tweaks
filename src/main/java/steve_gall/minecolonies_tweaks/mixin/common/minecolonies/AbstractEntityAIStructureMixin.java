package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.util.constant.CitizenConstants;
import com.minecolonies.core.entity.ai.workers.AbstractEntityAIStructure;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = AbstractEntityAIStructure.class, remap = false)
public abstract class AbstractEntityAIStructureMixin
{
	@ModifyConstant(method = "structureStep", remap = false, constant = @Constant(intValue = 150))
	private int getBuildBlockDelay0(int BUILD_BLOCK_DELAY)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.blockBuildingDelay.get() * CitizenConstants.PROGRESS_MULTIPLIER;
	}

	@Inject(method = "isBlockFree", remap = false, at = @At(value = "TAIL"), cancellable = true)
	private static void isBlockFree(BlockState block, CallbackInfoReturnable<Boolean> cir)
	{
		if (cir.getReturnValueZ() && block != null && block.is(BlockTags.LEAVES))
		{
			if (MCTweaksConfigServer.INSTANCE.jobs.structureLeavesFree.get())
			{
				return;
			}

			cir.setReturnValue(false);
		}

	}

}
