package steve_gall.minecolonies_tweaks.common.mixin.minecolonies;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.constant.IToolType;

import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.common.tool.CustomToolTypeData;
import steve_gall.minecolonies_tweaks.common.tool.ToolTypeExtension;

@Mixin(value = ItemStackUtils.class, remap = false)
public abstract class ItemStackUtilsMixin
{
	@Inject(method = "isTool", at = @At(value = "HEAD"), cancellable = true)
	private static void isTool(@Nullable final ItemStack itemStack, final IToolType toolType, CallbackInfoReturnable<Boolean> cir)
	{
		if (!ItemStackUtils.isEmpty(itemStack) && ToolTypeExtension.from(toolType).isCustomTool(itemStack))
		{
			cir.setReturnValue(true);
		}

	}

	@Inject(method = "getMiningLevel", at = @At(value = "HEAD"), cancellable = true)
	private static void getMiningLevel(@Nullable final ItemStack stack, @Nullable final IToolType toolType, CallbackInfoReturnable<Integer> cir)
	{
		if (ItemStackUtils.isTool(stack, toolType))
		{
			ToolTypeExtension extension = ToolTypeExtension.from(toolType);
			int level = extension.getCustomLevel(stack);

			if (level != -1)
			{
				cir.setReturnValue(level);
			}
			else
			{
				var data = CustomToolTypeData.find(toolType.getName());

				if (data != null)
				{
					data.getDefaultLevel().ifPresent(cir::setReturnValue);
				}

			}

		}

	}

}
