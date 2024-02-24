package steve_gall.minecolonies_tweaks.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.entity.ai.workers.AbstractEntityAIInteract;

import steve_gall.minecolonies_tweaks.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = AbstractEntityAIInteract.class, remap = false)
public class AbstractEntityAIInteractMixin
{
	@ModifyConstant(method = "getBlockMiningDelay", constant = @Constant(intValue = AbstractEntityAIInteract.BLOCK_MINING_DELAY / 2))
	private int getBlockMiningDelay0(int BLOCK_MINING_DELAY)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.commonBlockMiningDelay.get() / 2;
	}

	@ModifyConstant(method = "calculateWorkerMiningDelay", constant = @Constant(doubleValue = AbstractEntityAIInteract.BLOCK_MINING_DELAY))
	private double getBlockMiningDelay1(double BLOCK_MINING_DELAY)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.commonBlockMiningDelay.get();
	}

}
