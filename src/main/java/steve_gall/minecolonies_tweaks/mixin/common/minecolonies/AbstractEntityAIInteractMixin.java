package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.entity.ai.workers.AbstractEntityAIInteract;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = AbstractEntityAIInteract.class, remap = false)
public abstract class AbstractEntityAIInteractMixin
{
	@ModifyConstant(method = "getBlockMiningTime", remap = false, constant = @Constant(intValue = AbstractEntityAIInteract.BLOCK_MINING_DELAY / 2))
	private int getBlockMiningTime0(int BLOCK_MINING_DELAY)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.blockMiningDelay.get() / 2;
	}

	@ModifyConstant(method = "calculateWorkerMiningDelay", remap = false, constant = @Constant(doubleValue = AbstractEntityAIInteract.BLOCK_MINING_DELAY))
	private double getBlockMiningDelay1(double BLOCK_MINING_DELAY)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.blockMiningDelay.get();
	}

}
