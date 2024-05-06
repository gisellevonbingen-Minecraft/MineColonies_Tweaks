package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.workers.crafting.AbstractEntityAICrafting;

@Mixin(value = AbstractEntityAICrafting.class, remap = false)
public interface AbstractEntityAICraftingAccessor
{
	@Accessor(value = "PROGRESS_MULTIPLIER", remap = false)
	static int getProgressMultiplier()
	{
		throw new AssertionError();
	}

	@Accessor(value = "HITTING_TIME", remap = false)
	static int getHittingTime()
	{
		throw new AssertionError();
	}

}
