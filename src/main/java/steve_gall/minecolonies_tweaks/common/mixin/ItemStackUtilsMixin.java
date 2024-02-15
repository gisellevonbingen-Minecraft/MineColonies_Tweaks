package steve_gall.minecolonies_tweaks.common.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.constant.IToolType;

import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.common.tool.CustomToolTypeData;

@Mixin(value = ItemStackUtils.class, remap = false)
public abstract class ItemStackUtilsMixin
{
	@Inject(method = "isTool", at = @At(value = "TAIL"), cancellable = true)
	private static void isTool(@Nullable final ItemStack itemStack, final IToolType toolType, CallbackInfoReturnable<Boolean> cir)
	{
		var data = CustomToolTypeData.find(toolType.getName());

		if (data != null && data.isTool(itemStack))
		{
			cir.setReturnValue(true);
		}

	}

	@Inject(method = "getMiningLevel", at = @At(value = "HEAD"), cancellable = true)
	private static void getMiningLevel(@Nullable final ItemStack stack, @Nullable final IToolType toolType, CallbackInfoReturnable<Integer> cir)
	{
		var data = CustomToolTypeData.find(toolType.getName());

		if (data != null && ItemStackUtils.isTool(stack, toolType))
		{
			int level = data.getLevel(stack);

			if (level == -1)
			{
				data.getDefaultLevel().ifPresent(cir::setReturnValue);
			}
			else
			{
				cir.setReturnValue(level);
			}

		}

	}

}
