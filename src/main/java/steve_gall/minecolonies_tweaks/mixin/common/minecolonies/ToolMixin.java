package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.colony.requestsystem.requestable.Tool;
import com.minecolonies.api.util.constant.ToolType;

import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.common.tool.ToolTypeExtension;

@Mixin(value = Tool.class, remap = false)
public abstract class ToolMixin
{
	@Inject(method = "getToolClasses", remap = false, at = @At(value = "TAIL"), cancellable = true)
	private void getToolClasses(final ItemStack stack, CallbackInfoReturnable<Set<String>> cir)
	{
		for (var toolType : ToolType.values())
		{
			if (ToolTypeExtension.from(toolType).isCustomTool(stack))
			{
				cir.getReturnValue().add(toolType.getName());
			}

		}

	}

}
