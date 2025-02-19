package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.constant.IToolType;

import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.common.tool.ToolTypeExtension;
import steve_gall.minecolonies_tweaks.api.common.tool.ToolTypeTags;

@Mixin(value = ItemStackUtils.class, remap = false)
public abstract class ItemStackUtilsMixin
{
	@Inject(method = "isTool", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private static void isTool(@Nullable ItemStack itemStack, IToolType toolType, CallbackInfoReturnable<Boolean> cir)
	{
		if (!ItemStackUtils.isEmpty(itemStack))
		{
			if (ToolTypeExtension.from(toolType).isCustomTool(itemStack))
			{
				cir.setReturnValue(true);
			}
			else if (itemStack.is(ToolTypeTags.getBlacklist(toolType.getName())))
			{
				cir.setReturnValue(false);
			}

		}

	}

	@Inject(method = "getMiningLevel", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private static void getMiningLevel(@Nullable ItemStack itemStack, @Nullable IToolType toolType, CallbackInfoReturnable<Integer> cir)
	{
		if (ItemStackUtils.isTool(itemStack, toolType))
		{
			var extension = ToolTypeExtension.from(toolType);
			var level = extension.getCustomLevel(itemStack);

			if (level != -1)
			{
				cir.setReturnValue(level);
			}
			else
			{
				var data = extension.getCustomToolType();

				if (data != null)
				{
					data.getDefaultLevel().ifPresent(cir::setReturnValue);
				}

			}

		}

	}

}
