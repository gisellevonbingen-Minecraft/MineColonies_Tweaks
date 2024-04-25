package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies.AbstractEntityAICraftingAccessor;
import steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies.EntityAIWorkFarmerAccessor;
import steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies.EntityAIWorkSifterAccessor;

public class JobConfig
{
	public final IntValue craftingProgressMultiplier;
	public final IntValue craftingHittingTime;
	public final IntValue sifterProgressMultiplier;
	public final IntValue farmerWorkDelay;
	public final DoubleValue farmerSkillDivider;
	public final IntValue farmerActionsDoneUntilDumping;
	public final BooleanValue farmerPlantAfterHoe;
	public final BooleanValue farmerPlantAfterHarvest;

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

		builder.push("farmer");
		builder.comment("finalDelay = workDelay - (skillDivider * staminaLevel)");
		this.farmerWorkDelay = builder.defineInRange("workDelay", EntityAIWorkFarmerAccessor.getStandardDelay(), 0, Integer.MAX_VALUE);
		this.farmerSkillDivider = builder.defineInRange("skillDivider", 2.0D, 1.0D, Integer.MAX_VALUE);
		builder.comment("if havested count reached to this, farmer will go to dump");
		this.farmerActionsDoneUntilDumping = builder.defineInRange("actionsDoneUntilDumping", EntityAIWorkFarmerAccessor.getMaxBlocksMined(), EntityAIWorkFarmerAccessor.getMaxBlocksMined(), Integer.MAX_VALUE);
		builder.comment("whether plant seed after hoeing dirt");
		this.farmerPlantAfterHoe = builder.define("plantAfterHoe", true);
		builder.comment("whether plant seed after harvest crop");
		this.farmerPlantAfterHarvest = builder.define("plantAfterHarvest", true);
		builder.pop();
	}

}
