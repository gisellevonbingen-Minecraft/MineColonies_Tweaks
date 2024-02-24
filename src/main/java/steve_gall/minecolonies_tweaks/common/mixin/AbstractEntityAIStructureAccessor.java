package steve_gall.minecolonies_tweaks.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.workers.AbstractEntityAIStructure;

@Mixin(value = AbstractEntityAIStructure.class, remap = false)
public interface AbstractEntityAIStructureAccessor
{
	@Accessor(value = "BUILD_BLOCK_DELAY")
	static int getBuildBlockDelay()
	{
		throw new AssertionError();
	}

}
