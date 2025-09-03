package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import static com.minecolonies.api.entity.ai.statemachine.states.AIWorkerState.LUMBERJACK_GATHERING_2;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Utils;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingLumberjack;
import com.minecolonies.core.colony.jobs.JobLumberjack;
import com.minecolonies.core.entity.ai.workers.crafting.AbstractEntityAICrafting;
import com.minecolonies.core.entity.ai.workers.production.EntityAIWorkLumberjack;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

@Mixin(value = EntityAIWorkLumberjack.class, remap = false)
public abstract class EntityAIWorkLumberjackMixin extends AbstractEntityAICrafting<JobLumberjack, BuildingLumberjack>
{
	public EntityAIWorkLumberjackMixin(@NotNull JobLumberjack job)
	{
		super(job);
	}

	@WrapOperation(method = "chopTree", remap = false, at = @At(value = "INVOKE", target = "mineBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z", remap = false))
	private boolean chopTree_mineBlock(EntityAIWorkLumberjack self, BlockPos blockToMine, BlockPos safeStand, Operation<Boolean> operation)
	{
		var state = this.world.getBlockState(blockToMine);

		if (state.is(Blocks.CHORUS_FLOWER))
		{
			var result = this.mineBlock(blockToMine, safeStand, true, false, null);

			if (result)
			{
				var tool = this.worker.getMainHandItem();
				var rollTool = tool;

				if (!tool.isEmpty() && this.shouldSilkTouchBlock(state))
				{
					rollTool = tool.copy();
					rollTool.enchant(Utils.getRegistryValue(Enchantments.SILK_TOUCH, this.world), 1);
				}

				var localItems = Block.getDrops(state, this.world, blockToMine, null, this.worker, rollTool);
				localItems = this.increaseBlockDrops(localItems);

				for (var item : localItems)
				{
					InventoryUtils.transferItemStackIntoNextBestSlotInItemHandler(item, this.worker.getInventoryCitizen());
				}

				this.onBlockDropReception(localItems);
			}

			return result;
		}

		return operation.call(self, blockToMine, safeStand);
	}

	@Inject(method = "isItemWorthPickingUp", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void isItemWorthPickingUp(final ItemStack stack, CallbackInfoReturnable<Boolean> cir)
	{
		if (this.getState() == LUMBERJACK_GATHERING_2)
		{
			if (stack.is(Items.CHORUS_FLOWER))
			{
				cir.setReturnValue(true);
			}

		}

	}

	@WrapOperation(method = "isCorrectSapling", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/util/ItemStackUtils.isStackSapling", remap = false))
	private boolean isCorrectSapling_isStackSapling(ItemStack stack, Operation<Boolean> operation)
	{
		return operation.call(stack) || stack.is(Items.CHORUS_FLOWER);
	}

	@Inject(method = "placeSaplings", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void placeSaplings(int saplingSlot, ItemStack stack, Block block, CallbackInfo ci)
	{
		if (stack.is(Items.CHORUS_FLOWER))
		{
			var tree = this.job.getTree();

			while (!tree.getStumpLocations().isEmpty())
			{
				var pos = tree.getStumpLocations().get(0);

				if (this.world.setBlockAndUpdate(pos, block.defaultBlockState()) && !ItemStackUtils.isEmpty(getInventory().getStackInSlot(saplingSlot)))
				{
					this.getInventory().extractItem(saplingSlot, 1, false);
					tree.removeStump(pos);
				}
				else
				{
					return;
				}

			}

			ci.cancel();
		}

	}

}
