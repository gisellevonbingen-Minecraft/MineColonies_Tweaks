package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.core.entity.ai.workers.util.Tree;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import steve_gall.minecolonies_tweaks.core.common.block.ChrousTree;

@Mixin(value = Tree.class, remap = false)
public abstract class TreeMixin
{
	@Shadow(remap = false)
	LinkedList<BlockPos> woodBlocks;
	@Shadow(remap = false)
	BlockPos location;
	@Shadow(remap = false)
	BlockPos topLog;

	@Shadow(remap = false)
	abstract void addAndSearch(@NotNull final Level world, @NotNull final BlockPos log, @Nullable final IColony colony);

	@Inject(method = "addAndSearch", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void addAndSearch(Level level, BlockPos log, IColony colony, CallbackInfo ci)
	{
		var state = level.getBlockState(log);

		if (state.is(Blocks.CHORUS_PLANT) || state.is(Blocks.CHORUS_FLOWER))
		{
			var chrousTree = new ChrousTree(level, log);
			this.woodBlocks.addAll(chrousTree.getDeadFlowers());
			this.woodBlocks.add(chrousTree.getLog());
			this.location = chrousTree.getLog();
			this.topLog = chrousTree.getLog();
			ci.cancel();
		}

	}

	@Inject(method = "calcSapling", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void calcSapling(Level world, CallbackInfoReturnable<ItemStack> cir)
	{
		var state = world.getBlockState(this.topLog);

		if (state.is(Blocks.CHORUS_PLANT) || state.is(Blocks.CHORUS_FLOWER))
		{
			cir.setReturnValue(new ItemStack(Items.CHORUS_FLOWER));
		}

	}

	@Inject(method = "checkTree", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private static void checkTree(LevelReader level, BlockPos pos, List<ItemStorage> treesToNotCut, int dyntreesize, CallbackInfoReturnable<Boolean> cir)
	{
		var chorusSapling = new ItemStack(Blocks.CHORUS_FLOWER);

		for (var stack : treesToNotCut)
		{
			if (ItemStackUtils.compareItemStacksIgnoreStackSize(chorusSapling, stack.getItemStack()))
			{
				return;
			}

		}

		var chrousTree = new ChrousTree(level, pos);

		if (chrousTree.isChorusTree() && chrousTree.getAliveFlowers().size() == 0)
		{
			cir.setReturnValue(true);
		}

	}

}
