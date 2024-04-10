package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.workers.crafting.EntityAIWorkSifter;

@Mixin(value = EntityAIWorkSifter.class, remap = false)
public interface EntityAIWorkSifterAccessor
{
	@Accessor("MAX_LEVEL")
	static int getMaxLevel()
	{
		throw new AssertionError();
	}

}
