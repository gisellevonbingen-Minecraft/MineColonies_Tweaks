package steve_gall.minecolonies_tweaks.common.config;

import com.minecolonies.core.entity.ai.workers.AbstractEntityAIInteract;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import steve_gall.minecolonies_tweaks.common.mixin.AbstractEntityAICraftingAccessor;
import steve_gall.minecolonies_tweaks.common.mixin.AbstractEntityAIStructureAccessor;
import steve_gall.minecolonies_tweaks.common.mixin.EntityAIStructureBuilderAccessor;
import steve_gall.minecolonies_tweaks.common.mixin.EntityAIWorkSifterAccessor;

public class JobConfig
{
	public final IntValue commonBlockMiningDelay;
	public final IntValue commonBlockBuildingDelay;

	public final DoubleValue builderBlockMiningDelayBuff;
	public final IntValue craftingProgressMultiplier;
	public final IntValue craftingHittingTime;
	public final IntValue sifterProgressMultiplier;

	public JobConfig(ForgeConfigSpec.Builder builder)
	{
		this.commonBlockMiningDelay = builder.defineInRange("blockMiningDelay", AbstractEntityAIInteract.BLOCK_MINING_DELAY, 0, Integer.MAX_VALUE);
		this.commonBlockBuildingDelay = builder.defineInRange("blockBuildingDelay", AbstractEntityAIStructureAccessor.getBuildBlockDelay(), 0, Integer.MAX_VALUE);

		builder.push("builder");
		builder.comment("builder's blockMiningDelay = commonBlockingMiningDelay * buff");
		this.builderBlockMiningDelayBuff = builder.defineInRange("blockMiningDelayBuff", EntityAIStructureBuilderAccessor.getSpeedBuff0(), 0.0D, 1.0D);
		builder.pop();

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
