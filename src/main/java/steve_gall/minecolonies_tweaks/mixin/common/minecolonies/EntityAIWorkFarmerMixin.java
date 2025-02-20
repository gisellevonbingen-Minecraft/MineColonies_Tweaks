package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.entity.ai.statemachine.states.AIWorkerState;
import com.minecolonies.api.entity.ai.statemachine.states.IAIState;
import com.minecolonies.core.colony.buildings.modules.FieldsModule;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingFarmer;
import com.minecolonies.core.colony.fields.FarmField;
import com.minecolonies.core.colony.jobs.JobFarmer;
import com.minecolonies.core.entity.ai.workers.crafting.AbstractEntityAICrafting;
import com.minecolonies.core.entity.ai.workers.production.agriculture.EntityAIWorkFarmer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.StemBlock;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = EntityAIWorkFarmer.class, remap = false)
public abstract class EntityAIWorkFarmerMixin extends AbstractEntityAICrafting<JobFarmer, BuildingFarmer>
{
	@Shadow(remap = false)
	private boolean shouldDumpInventory;

	@Unique
	private BlockPos minecolonies_tweaks$workingPosition;

	public EntityAIWorkFarmerMixin(@NotNull JobFarmer job)
	{
		super(job);
	}

	@Shadow(remap = false)
	public abstract boolean hoeIfAble(BlockPos position, FarmField farmField);

	@Shadow(remap = false)
	public abstract boolean tryToPlant(FarmField farmField, BlockPos position);

	@Shadow(remap = false)
	public abstract boolean harvestIfAble(BlockPos position);

	@Inject(method = "workAtField", remap = false, at = @At(value = "RETURN"), cancellable = true)
	private void workAtField(CallbackInfoReturnable<IAIState> cir)
	{
		if (MineColoniesTweaksConfigServer.INSTANCE.fields.newRetrieveMethod.get().booleanValue() && cir.getReturnValue() == AIWorkerState.IDLE)
		{
			if (this.shouldDumpInventory)
			{
				this.shouldDumpInventory = false;
				cir.setReturnValue(AIWorkerState.INVENTORY_FULL);
			}

		}

	}

	@Inject(method = "hoeIfAble", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void hoeIfAble_HEAD(BlockPos position, FarmField farmField, CallbackInfoReturnable<Boolean> cir)
	{
		this.minecolonies_tweaks$workingPosition = position;
	}

	@Inject(method = "hoeIfAble", remap = false, at = @At(value = "RETURN"), cancellable = true)
	private void hoeIfAble_Return(BlockPos position, FarmField farmField, CallbackInfoReturnable<Boolean> cir)
	{
		if (MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerPlantAfterHoe.get().booleanValue())
		{
			if (farmField.getSeed().getItem() instanceof BlockItem item && item.getBlock() instanceof StemBlock)
			{
				return;
			}

			this.tryToPlant(farmField, this.minecolonies_tweaks$workingPosition);
		}

	}

	@Inject(method = "harvestIfAble", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void harvestIfAble_Head(@NotNull BlockPos position, CallbackInfoReturnable<Boolean> cir)
	{
		this.minecolonies_tweaks$workingPosition = position;
	}

	@Inject(method = "harvestIfAble", remap = false, at = @At(value = "RETURN"), cancellable = true)
	private void harvestIfAble_Return(@NotNull BlockPos position, CallbackInfoReturnable<Boolean> cir)
	{
		if (cir.getReturnValueZ() && position != null)
		{
			if (MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerPlantAfterHarvest.get().booleanValue())
			{
				if (this.building.getFirstModuleOccurance(FieldsModule.class).getCurrentField() instanceof FarmField farmField)
				{
					if (farmField.getSeed().getItem() instanceof BlockItem item && item.getBlock() instanceof StemBlock)
					{
						return;
					}

					this.tryToPlant(farmField, this.minecolonies_tweaks$workingPosition);
				}

			}

		}

	}

	@ModifyConstant(method = "getLevelDelay", remap = false, constant = @Constant(doubleValue = 40))
	private double getLevelDelay_standardDelay(double STANDARD_DELAY)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerWorkDelay.get().intValue();
	}

	@ModifyConstant(method = "getLevelDelay", remap = false, constant = @Constant(doubleValue = 2.0))
	private double getLevelDelay_skillDivider(double skillDivider)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerSkillDivider.get().doubleValue();
	}

	@Redirect(method = "wantInventoryDumped", remap = false, at = @At(value = "INVOKE", target = "getActionRewardForCraftingSuccess", remap = false))
	private int wantInventoryDumped_getActionRewardForCraftingSuccess(EntityAIWorkFarmer self)
	{
		return this.getActionsDoneUntilDumping();
	}

	@ModifyConstant(method = "getActionsDoneUntilDumping", remap = false, constant = @Constant(intValue = 64))
	private int getActionsDoneUntilDumping(int MAX_BLOCKS_MINED)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.jobs.farmerActionsDoneUntilDumping.get().intValue();
	}

}
