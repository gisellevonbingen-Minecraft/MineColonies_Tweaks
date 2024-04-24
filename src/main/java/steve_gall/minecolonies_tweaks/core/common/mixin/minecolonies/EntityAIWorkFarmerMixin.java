package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.compatibility.Compatibility;
import com.minecolonies.core.colony.buildings.modules.FieldsModule;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingFarmer;
import com.minecolonies.core.colony.fields.FarmField;
import com.minecolonies.core.colony.jobs.JobFarmer;
import com.minecolonies.core.entity.ai.basic.AbstractEntityAICrafting;
import com.minecolonies.core.entity.ai.citizen.farmer.EntityAIWorkFarmer;

import net.minecraft.core.BlockPos;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = EntityAIWorkFarmer.class, remap = false)
public abstract class EntityAIWorkFarmerMixin extends AbstractEntityAICrafting<JobFarmer, BuildingFarmer>
{
	@Unique
	private BlockPos minecolonies_tweaks$workingPosition;

	public EntityAIWorkFarmerMixin(@NotNull JobFarmer job)
	{
		super(job);
	}

	@Shadow
	private boolean hoeIfAble(BlockPos position, FarmField farmField)
	{
		throw new AssertionError();
	}

	@Shadow
	private boolean tryToPlant(FarmField farmField, BlockPos position)
	{
		throw new AssertionError();
	}

	@Shadow
	private boolean harvestIfAble(BlockPos position)
	{
		throw new AssertionError();
	}

	@Inject(method = "hoeIfAble", at = @At(value = "HEAD"), cancellable = true)
	private void hoeIfAble_HEAD(BlockPos position, FarmField farmField, CallbackInfoReturnable<Boolean> cir)
	{
		this.minecolonies_tweaks$workingPosition = position;
	}

	@Inject(method = "hoeIfAble", at = @At(value = "RETURN"), cancellable = true)
	private void hoeIfAble_Return(BlockPos position, FarmField farmField, CallbackInfoReturnable<Boolean> cir)
	{
		if (MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerPlantAfterHoe.get().booleanValue())
		{
			this.tryToPlant(farmField, this.minecolonies_tweaks$workingPosition);
		}

	}

	@Inject(method = "harvestIfAble", at = @At(value = "HEAD"), cancellable = true)
	private void harvestIfAble_Head(@NotNull BlockPos position, CallbackInfoReturnable<Boolean> cir)
	{
		this.minecolonies_tweaks$workingPosition = position;
	}

	@Inject(method = "harvestIfAble", at = @At(value = "RETURN"), cancellable = true)
	private void harvestIfAble_Return(@NotNull BlockPos position, CallbackInfoReturnable<Boolean> cir)
	{
		if (cir.getReturnValueZ() && !Compatibility.isPamsInstalled() && position != null)
		{
			if (MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerPlantAfterHarvest.get().booleanValue())
			{
				if (this.building.getFirstModuleOccurance(FieldsModule.class).getCurrentField() instanceof FarmField farmField)
				{
					this.tryToPlant(farmField, this.minecolonies_tweaks$workingPosition);
				}

			}

		}

	}

	@ModifyConstant(method = "getLevelDelay", constant = @Constant(doubleValue = 40))
	private double getLevelDelay_standardDelay(double STANDARD_DELAY)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerWorkDelay.get().intValue();
	}

	@ModifyConstant(method = "getLevelDelay", constant = @Constant(doubleValue = 2.0))
	private double getLevelDelay_skillDivider(double skillDivider)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerSkillDivider.get().doubleValue();
	}

	@ModifyConstant(method = "getActionRewardForCraftingSuccess", constant = @Constant(intValue = 64))
	private int getActionRewardForCraftingSuccess(int MAX_BLOCKS_MINED)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerActionsDoneUntilDumping.get().intValue();
	}

	@ModifyConstant(method = "getActionsDoneUntilDumping", constant = @Constant(intValue = 64))
	private int getActionsDoneUntilDumping(int MAX_BLOCKS_MINED)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerActionsDoneUntilDumping.get().intValue();
	}

}
