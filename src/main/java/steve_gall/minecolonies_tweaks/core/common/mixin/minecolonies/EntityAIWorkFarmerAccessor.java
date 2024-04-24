package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.workers.production.agriculture.EntityAIWorkFarmer;

@Mixin(value = EntityAIWorkFarmer.class, remap = false)
public interface EntityAIWorkFarmerAccessor
{
	@Accessor("STANDARD_DELAY")
	static int getStandardDelay()
	{
		throw new AssertionError();
	}

	@Accessor("MAX_BLOCKS_MINED")
	static int getMaxBlocksMined()
	{
		throw new AssertionError();
	}

}
