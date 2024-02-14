package steve_gall.minecolonies_tweaks.common.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.colony.requestsystem.requestable.Tool;

import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.common.tool.CustomToolTypeData;

@Mixin(value = Tool.class, remap = false)
public abstract class ToolMixin
{
	@Inject(method = "getToolClasses", at = @At(value = "TAIL"), cancellable = true)
	private void getToolClasses(final ItemStack stack, CallbackInfoReturnable<Set<String>> cir)
	{
		for (CustomToolTypeData data : CustomToolTypeData.list())
		{
			if (stack.is(data.getItemTag()))
			{
				cir.getReturnValue().add(data.getName());
			}

		}

	}

}
