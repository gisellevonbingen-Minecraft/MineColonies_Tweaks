package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.util.ItemStackUtils;

import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.common.tool.ToolTypeExtension;
import steve_gall.minecolonies_tweaks.api.common.tool.ToolTypeTags;

@Mixin(value = EquipmentTypeEntry.class, remap = false)
public abstract class EquipmentTypeEntryMixin
{
	@Inject(method = "checkIsEquipment", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void checkIsEquipment(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir)
	{
		var toolType = (EquipmentTypeEntry) (Object) this;

		if (!ItemStackUtils.isEmpty(itemStack))
		{
			if (ToolTypeTags.isInBlacklist(itemStack, toolType.getRegistryName()))
			{
				cir.setReturnValue(false);
			}
			else if (ToolTypeExtension.from(toolType).isCustomTool(itemStack))
			{
				cir.setReturnValue(true);
			}

		}

	}

	@Inject(method = "getMiningLevel", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void getMiningLevel(ItemStack itemStack, CallbackInfoReturnable<Integer> cir)
	{
		var toolType = (EquipmentTypeEntry) (Object) this;

		if (toolType.checkIsEquipment(itemStack))
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
					cir.setReturnValue(data.getDefaultLevel());
				}

			}

		}

	}

}
