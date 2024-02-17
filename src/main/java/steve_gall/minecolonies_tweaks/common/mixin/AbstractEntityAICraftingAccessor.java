package steve_gall.minecolonies_tweaks.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.basic.AbstractEntityAICrafting;

@Mixin(value = AbstractEntityAICrafting.class, remap = false)
public interface AbstractEntityAICraftingAccessor
{
	@Accessor(value = "PROGRESS_MULTIPLIER")
	static int getProgressMultiplier()
	{
		throw new AssertionError();
	}

	@Accessor(value = "HITTING_TIME")
	static int getHittingTime()
	{
		throw new AssertionError();
	}

}
