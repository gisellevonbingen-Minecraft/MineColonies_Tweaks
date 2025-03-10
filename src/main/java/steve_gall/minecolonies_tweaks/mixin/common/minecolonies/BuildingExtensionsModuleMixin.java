package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.core.colony.buildings.modules.BuildingExtensionsModule;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = BuildingExtensionsModule.class, remap = false)
public abstract class BuildingExtensionsModuleMixin
{
	@Shadow(remap = false)
	@Nullable
	private IBuildingExtension currentExtension;

	@Unique
	private int minecolonies_tweaks$nextIndex;

	@Shadow(remap = false)
	@NotNull
	public abstract List<IBuildingExtension> getOwnedExtensions();

	@Inject(method = "getExtensionToWorkOn", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void getExtensionToWorkOn(CallbackInfoReturnable<IBuildingExtension> cir)
	{
		if (!MineColoniesTweaksConfigServer.INSTANCE.fields.newRetrieveMethod.get().booleanValue())
		{
			return;
		}

		if (this.currentExtension != null)
		{
			cir.setReturnValue(this.currentExtension);
			return;
		}

		var fields = this.getOwnedExtensions();
		int fieldsSize = fields.size();

		if (fieldsSize == 0)
		{
			cir.setReturnValue(null);
			return;
		}

		if (0 >= this.minecolonies_tweaks$nextIndex || this.minecolonies_tweaks$nextIndex >= fieldsSize)
		{
			this.minecolonies_tweaks$nextIndex = 0;
		}

		this.currentExtension = fields.get(this.minecolonies_tweaks$nextIndex);
		this.minecolonies_tweaks$nextIndex = this.minecolonies_tweaks$nextIndex + 1;

		cir.setReturnValue(this.currentExtension);
	}

}
