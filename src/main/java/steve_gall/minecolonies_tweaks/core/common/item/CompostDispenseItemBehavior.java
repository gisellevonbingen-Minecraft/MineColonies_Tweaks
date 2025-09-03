package steve_gall.minecolonies_tweaks.core.common.item;

import com.minecolonies.core.blocks.MinecoloniesCropBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;

public class CompostDispenseItemBehavior extends OptionalDispenseItemBehavior
{
	@Override
	@SuppressWarnings("deprecation")
	protected ItemStack execute(BlockSource source, ItemStack stack)
	{
		this.setSuccess(true);

		var level = source.level();
		var pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
		var state = level.getBlockState(pos);

		if (!this.growCrop(level, pos, state, stack) && !BoneMealItem.growCrop(stack, level, pos))
		{
			this.setSuccess(false);
		}
		else if (!level.isClientSide)
		{
			level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 15);
		}

		return stack;
	}

	public boolean growCrop(ServerLevel level, BlockPos pos, BlockState state, ItemStack stack)
	{
		if (state.getBlock() instanceof MinecoloniesCropBlock crop && !crop.isMaxAge(state))
		{
			stack.shrink(1);
			crop.attemptGrow(state, level, pos);
			return true;
		}

		return false;
	}

}
