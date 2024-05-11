package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.workers.AbstractEntityAIStructure;

@Mixin(value = AbstractEntityAIStructure.class, remap = false)
public interface AbstractEntityAIStructureAccessor
{
	@Accessor(value = "BUILD_BLOCK_DELAY", remap = false)
	static int getBuildBlockDelay()
	{
		throw new AssertionError();
	}

}
