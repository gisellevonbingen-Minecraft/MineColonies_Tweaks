package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.entity.ai.workers.builder.EntityAIStructureBuilder;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = EntityAIStructureBuilder.class, remap = false)
public abstract class EntityAIStructureBuilderMixin
{
	@ModifyConstant(method = "getBlockMiningDelay", remap = false, constant = @Constant(doubleValue = 0.5D))
	private double getBlockMiningDelayBuff(double SPEED_BUFF_0)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.builderBlockMiningDelayBuff.get();
	}

}
