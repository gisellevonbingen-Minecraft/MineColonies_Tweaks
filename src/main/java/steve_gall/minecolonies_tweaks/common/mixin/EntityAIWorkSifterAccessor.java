package steve_gall.minecolonies_tweaks.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.citizen.sifter.EntityAIWorkSifter;

@Mixin(value = EntityAIWorkSifter.class, remap = false)
public interface EntityAIWorkSifterAccessor
{
	@Accessor("MAX_LEVEL")
	static int getMaxLevel()
	{
		throw new AssertionError();
	}

}
