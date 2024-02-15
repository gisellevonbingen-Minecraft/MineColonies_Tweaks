package steve_gall.minecolonies_tweaks.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.basic.AbstractEntityAICrafting;

@Mixin(value = AbstractEntityAICrafting.class, remap = false)
public interface AbstractEntityAICraftingAccessor
{
	@Accessor("PROGRESS_MULTIPLIER")
	public static int getProgressMultiplier()
	{
		throw new AssertionError();
	}

	@Accessor("PROGRESS_MULTIPLIER")
	public static void setProgressMultiplier(int progressMultiplier)
	{
		throw new AssertionError();
	}

	@Accessor("HITTING_TIME")
	public static int getHittingTime()
	{
		throw new AssertionError();
	}

	@Accessor("HITTING_TIME")
	public static void setHittingTime(int hittingTime)
	{
		throw new AssertionError();
	}

}
