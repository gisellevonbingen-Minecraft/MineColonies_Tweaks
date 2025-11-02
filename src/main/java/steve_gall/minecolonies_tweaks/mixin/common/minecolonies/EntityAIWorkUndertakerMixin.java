package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.minecolonies.api.util.constant.UndertakerConstants;
import com.minecolonies.core.entity.ai.workers.service.EntityAIWorkUndertaker;

import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = EntityAIWorkUndertaker.class, remap = false)
public abstract class EntityAIWorkUndertakerMixin
{
	@ModifyConstant(method = "getTotemResurrectChance", remap = false, constant = @Constant(doubleValue = UndertakerConstants.SINGLE_TOTEM_RESURRECTION_CHANCE_BONUS, ordinal = 0))
	private double getTotemResurrectChance_Single(double SINGLE_TOTEM_RESURRECTION_CHANCE_BONUS)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.undertakerResurrectTotemSingleChance.get();
	}

	@ModifyConstant(method = "getTotemResurrectChance", remap = false, constant = @Constant(doubleValue = UndertakerConstants.MULTIPLE_TOTEMS_RESURRECTION_CHANCE_BONUS, ordinal = 0))
	private double getTotemResurrectChance_Multiple(double MULTIPLE_TOTEMS_RESURRECTION_CHANCE_BONUS)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.undertakerResurrectTotemMultipleChance.get();
	}

	@ModifyConstant(method = "getResurrectChance", remap = false, constant = @Constant(doubleValue = UndertakerConstants.RESURRECT_BUILDING_LVL_WEIGHT, ordinal = 0))
	private double getResurrectChance_BuildingLevelWeight(double RESURRECT_BUILDING_LVL_WEIGHT)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.undertakerResurrectBuildingLevelWeight.get();
	}

	@ModifyConstant(method = "getResurrectChance", remap = false, constant = @Constant(doubleValue = UndertakerConstants.RESURRECT_WORKER_MANA_LVL_WEIGHT, ordinal = 0))
	private double getResurrectChance_ManaLevelWeight(double RESURRECT_WORKER_MANA_LVL_WEIGHT)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.undertakerResurrectManaLevelWeight.get();
	}

	@ModifyConstant(method = "getResurrectChance", remap = false, constant = @Constant(doubleValue = UndertakerConstants.MAX_RESURRECTION_CHANCE, ordinal = 0))
	private double getResurrectChance_CapBase(double MAX_RESURRECTION_CHANCE)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.undertakerResurrectCapDisable.get() ? 1.0D : MCTweaksConfigServer.INSTANCE.jobs.undertakerResurrectCapBase.get();
	}

	@ModifyConstant(method = "getResurrectChance", remap = false, constant = @Constant(doubleValue = UndertakerConstants.MAX_RESURRECTION_CHANCE_MYSTICAL_LVL_BONUS, ordinal = 1))
	private double getResurrectChance_CapMysticalLevelWeight(double MAX_RESURRECTION_CHANCE_MYSTICAL_LVL_BONUS)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.undertakerResurrectCapMysticalLevelWeight.get();
	}

	@ModifyConstant(method = "tryResurrect", remap = false, constant = @Constant(doubleValue = UndertakerConstants.TOTEM_BREAK_CHANCE, ordinal = 0))
	private double tryResurrect_TotemBreakChance(double TOTEM_BREAK_CHANCE)
	{
		return MCTweaksConfigServer.INSTANCE.jobs.undertakerResurrectTotemBreakChance.get();
	}

}
