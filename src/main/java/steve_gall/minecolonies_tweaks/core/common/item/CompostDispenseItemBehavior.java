package steve_gall.minecolonies_tweaks.core.common.item;

import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;

public class CompostDispenseItemBehavior extends OptionalDispenseItemBehavior
{
	@Override
	@SuppressWarnings("deprecation")
	protected ItemStack execute(BlockSource source, ItemStack stack)
	{
		this.setSuccess(true);

		var level = source.getLevel();
		var pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));

		if (!BoneMealItem.growCrop(stack, level, pos))
		{
			this.setSuccess(false);
		}
		else if (!level.isClientSide)
		{
			level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 0);
		}

		return stack;
	}

}
