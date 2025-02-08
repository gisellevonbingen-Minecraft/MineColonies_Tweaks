package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.core.entity.ai.basic.AbstractEntityAIStructure;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = AbstractEntityAIStructure.class, remap = false)
public abstract class AbstractEntityAIStructureMixin
{
	@Inject(method = "isBlockFree", remap = false, at = @At(value = "TAIL"), cancellable = true)
	private static void isBlockFree(BlockState block, CallbackInfoReturnable<Boolean> cir)
	{
		if (cir.getReturnValueZ() && block != null && block.is(BlockTags.LEAVES))
		{
			if (MineColoniesTweaksConfigServer.INSTANCE.jobs.structureLeavesFree.get())
			{
				return;
			}

			cir.setReturnValue(false);
		}

	}

}
