package steve_gall.minecolonies_tweaks.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import steve_gall.minecolonies_tweaks.common.mixin.AbstractEntityAICraftingAccessor;
import steve_gall.minecolonies_tweaks.common.mixin.EntityAIWorkSifterAccessor;

public class JobConfig
{
	public final IntValue craftingProgressMultiplier;
	public final IntValue craftingHittingTime;
	public final IntValue sifterProgressMultiplier;

	public JobConfig(ForgeConfigSpec.Builder builder)
	{
		builder.push("crafing");
		builder.comment("craftingTicks = progressMultiplier / craftSkillLevel * hittingTime");
		this.craftingProgressMultiplier = builder.defineInRange("progressMultiplier", AbstractEntityAICraftingAccessor.getProgressMultiplier(), 0, Integer.MAX_VALUE);
		this.craftingHittingTime = builder.defineInRange("hittingTime", AbstractEntityAICraftingAccessor.getHittingTime(), 0, Integer.MAX_VALUE);
		builder.pop();

		builder.push("sifter");
		builder.comment("siftingTicks = progressMultiplier - strengthLevel");
		this.sifterProgressMultiplier = builder.defineInRange("progressMultiplier", EntityAIWorkSifterAccessor.getMaxLevel(), 0, Integer.MAX_VALUE);
		builder.pop();
	}

}
