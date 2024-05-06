package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.citizen.sifter.EntityAIWorkSifter;

@Mixin(value = EntityAIWorkSifter.class, remap = false)
public interface EntityAIWorkSifterAccessor
{
	@Accessor(value = "MAX_LEVEL", remap = false)
	static int getMaxLevel()
	{
		throw new AssertionError();
	}

}
