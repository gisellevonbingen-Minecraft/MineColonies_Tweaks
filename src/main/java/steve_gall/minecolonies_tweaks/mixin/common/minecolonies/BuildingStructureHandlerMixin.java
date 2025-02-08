package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.core.entity.ai.util.BuildingStructureHandler;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = BuildingStructureHandler.class, remap = false)
public abstract class BuildingStructureHandlerMixin
{
	@Inject(method = "isStackFree", remap = false, at = @At(value = "TAIL"), cancellable = true)
	private void isStackFree(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir)
	{
		if (cir.getReturnValueZ() && itemStack != null && itemStack.is(ItemTags.LEAVES))
		{
			if (MineColoniesTweaksConfigServer.INSTANCE.jobs.structureLeavesFree.get())
			{
				return;
			}

			cir.setReturnValue(false);
		}

	}

}
