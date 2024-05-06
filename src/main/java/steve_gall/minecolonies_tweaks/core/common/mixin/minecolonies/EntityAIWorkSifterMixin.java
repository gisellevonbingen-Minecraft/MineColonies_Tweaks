package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.core.entity.ai.citizen.sifter.EntityAIWorkSifter;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = EntityAIWorkSifter.class, remap = false)
public abstract class EntityAIWorkSifterMixin
{
	@ModifyConstant(method = "sift", remap = false, constant = @Constant(intValue = 50))
	private int modifyMaxLevel(int MAX_LEVEL)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.sifterProgressMultiplier.get();
	}

}
