package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.workers.production.agriculture.EntityAIWorkFarmer;

@Mixin(value = EntityAIWorkFarmer.class, remap = false)
public interface EntityAIWorkFarmerAccessor
{
	@Accessor(value = "DEFAULT_DELAY", remap = false)
	static int getStandardDelay()
	{
		throw new AssertionError();
	}

	@Accessor(value = "MAX_BLOCKS_MINED", remap = false)
	static int getMaxBlocksMined()
	{
		throw new AssertionError();
	}

}
