package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries.BuildingExtensionEntry;
import com.minecolonies.core.colony.buildingextensions.FarmField;

import net.minecraft.core.BlockPos;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = FarmField.class, remap = false)
public abstract class FarmFieldMixin
{
	@Shadow(remap = false)
	private int maxRadius;

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"))
	private void init(BuildingExtensionEntry fieldType, BlockPos position, CallbackInfo ci)
	{
		this.maxRadius = MineColoniesTweaksConfigServer.INSTANCE.fields.farmMaxRange.get().intValue();
	}

}
