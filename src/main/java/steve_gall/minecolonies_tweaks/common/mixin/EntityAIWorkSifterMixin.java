package steve_gall.minecolonies_tweaks.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.entity.ai.workers.crafting.EntityAIWorkSifter;

import steve_gall.minecolonies_tweaks.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = EntityAIWorkSifter.class, remap = false)
public class EntityAIWorkSifterMixin
{
	@ModifyConstant(method = "sift", constant = @Constant(intValue = 50))
	private int modifyMaxLevel(int MAX_LEVEL)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.sifterProgressMultiplier.get();
	}

}
