package steve_gall.minecolonies_tweaks.core.common.config;

import com.minecolonies.api.util.constant.UndertakerConstants;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.AbstractEntityAICraftingAccessor;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.EntityAIWorkFarmerAccessor;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.EntityAIWorkSifterAccessor;

public class JobConfig
{
	public final IntValue craftingProgressMultiplier;
	public final IntValue craftingHittingTime;
	public final IntValue sifterProgressMultiplier;
	public final BooleanValue dyerDisableBleaching;
	public final IntValue farmerWorkDelay;
	public final DoubleValue farmerSkillDivider;
	public final IntValue farmerActionsDoneUntilDumping;
	public final BooleanValue farmerPlantAfterHoe;
	public final BooleanValue farmerPlantAfterHarvest;
	public final BooleanValue structureLeavesFree;
	public final IntValue warehouseCouriersPerLevel;
	public final IntValue maximumStockKindsPerLevel;
	public final DoubleValue undertakerResurrectTotemSingleChance;
	public final DoubleValue undertakerResurrectTotemMultipleChance;
	public final DoubleValue undertakerResurrectBuildingLevelWeight;
	public final DoubleValue undertakerResurrectManaLevelWeight;
	public final BooleanValue undertakerResurrectCapDisable;
	public final DoubleValue undertakerResurrectCapBase;
	public final DoubleValue undertakerResurrectCapMysticalLevelWeight;
	public final DoubleValue undertakerResurrectTotemBreakChance;

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

		builder.push("dyer");
		builder.comment("disables wool bleaching to avoid wool loop crafting. To take effect it requires using the command \"/mc colony requestsystem-reset-all\"");
		builder.comment(ConfigConstants.VANILLA_IS_FALSE);
		this.dyerDisableBleaching = builder.define("dyerDisableBleaching", true);
		builder.pop();

		builder.push("farmer");
		builder.comment("finalDelay = workDelay - (skillDivider * staminaLevel)");
		this.farmerWorkDelay = builder.defineInRange("workDelay", EntityAIWorkFarmerAccessor.getStandardDelay(), 0, Integer.MAX_VALUE);
		this.farmerSkillDivider = builder.defineInRange("skillDivider", 2.0D, 0.0D, Integer.MAX_VALUE);
		builder.comment("if harvested count reached to this, farmer will go to dump");
		this.farmerActionsDoneUntilDumping = builder.defineInRange("actionsDoneUntilDumping", 256, EntityAIWorkFarmerAccessor.getMaxBlocksMined(), Integer.MAX_VALUE);
		builder.comment("whether plant seed after hoeing dirt");
		builder.comment(ConfigConstants.VANILLA_IS_FALSE);
		this.farmerPlantAfterHoe = builder.define("plantAfterHoe", true);
		builder.comment("whether plant seed after harvest crop");
		builder.comment(ConfigConstants.VANILLA_IS_FALSE);
		this.farmerPlantAfterHarvest = builder.define("plantAfterHarvest", true);
		builder.pop();

		builder.push("undertaker");
		builder.push("resurrectChance");
		this.undertakerResurrectTotemSingleChance = builder.defineInRange("totemSingleChance", UndertakerConstants.SINGLE_TOTEM_RESURRECTION_CHANCE_BONUS, 0.0D, 1.0D);
		this.undertakerResurrectTotemMultipleChance = builder.defineInRange("totemMultipleChance", UndertakerConstants.MULTIPLE_TOTEMS_RESURRECTION_CHANCE_BONUS, 0.0D, 1.0D);
		this.undertakerResurrectBuildingLevelWeight = builder.defineInRange("buildingLevelWeight", UndertakerConstants.RESURRECT_BUILDING_LVL_WEIGHT, 0.0D, 1.0D);
		this.undertakerResurrectManaLevelWeight = builder.defineInRange("manaLevelWeight", UndertakerConstants.RESURRECT_WORKER_MANA_LVL_WEIGHT, 0.0D, 1.0D);
		builder.pop();
		builder.push("resurrectChanceCap");
		this.undertakerResurrectCapDisable = builder.define("disable", true);
		this.undertakerResurrectCapBase = builder.defineInRange("base", UndertakerConstants.MAX_RESURRECTION_CHANCE, 0.0D, 1.0D);
		this.undertakerResurrectCapMysticalLevelWeight = builder.defineInRange("mysticalLevelWeight", UndertakerConstants.MAX_RESURRECTION_CHANCE_MYSTICAL_LVL_BONUS, 0.0D, 1.0D);
		builder.pop();
		this.undertakerResurrectTotemBreakChance = builder.defineInRange("resurrectTotemBreakChance", UndertakerConstants.TOTEM_BREAK_CHANCE, 0.0D, 1.0D);
		builder.pop();

		builder.push("structure");
		builder.comment("If this is off, Builders will requests 'Leaves' on needed it when build.");
		this.structureLeavesFree = builder.define("leavesFree", true);
		builder.pop();

		builder.push("warehouse");
		this.warehouseCouriersPerLevel = builder.defineInRange("couriersPerLevel", 2, 1, 20);
		builder.pop();

		builder.push("maximumStock");
		builder.comment("This will be affected by research effect 'effects/minimumstockmultiplier'");
		this.maximumStockKindsPerLevel = builder.defineInRange("kindsPerLevel", 5, 0, 100);
		builder.pop();
	}

}
