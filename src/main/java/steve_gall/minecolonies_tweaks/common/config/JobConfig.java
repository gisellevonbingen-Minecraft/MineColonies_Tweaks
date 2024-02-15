package steve_gall.minecolonies_tweaks.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import steve_gall.minecolonies_tweaks.common.mixin.AbstractEntityAICraftingAccessor;

public class JobConfig
{
	public final IntValue craftingHittingTime;
	public final IntValue craftingProgressMultiplier;

	public JobConfig(ForgeConfigSpec.Builder builder)
	{
		this.craftingHittingTime = builder.defineInRange("craftingHittingTime", AbstractEntityAICraftingAccessor.getHittingTime(), 0, Integer.MAX_VALUE);
		this.craftingProgressMultiplier = builder.defineInRange("craftingProgressMultiplier", AbstractEntityAICraftingAccessor.getProgressMultiplier(), 0, Integer.MAX_VALUE);
	}

}
