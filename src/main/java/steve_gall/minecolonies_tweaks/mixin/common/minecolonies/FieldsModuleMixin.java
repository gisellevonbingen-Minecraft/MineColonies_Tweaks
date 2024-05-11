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

import com.minecolonies.api.colony.fields.IField;
import com.minecolonies.core.colony.buildings.modules.FieldsModule;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = FieldsModule.class, remap = false)
public abstract class FieldsModuleMixin
{
	@Shadow(remap = false)
	@Nullable
	private IField currentField;

	@Unique
	private int minecolonies_tweaks$nextIndex;

	@Shadow(remap = false)
	@NotNull
	public abstract List<IField> getOwnedFields();

	@Inject(method = "getFieldToWorkOn", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void getFieldToWorkOn(CallbackInfoReturnable<IField> cir)
	{
		if (!MineColoniesTweaksConfigServer.INSTANCE.fields.newRetrieveMethod.get().booleanValue())
		{
			return;
		}

		if (this.currentField != null)
		{
			cir.setReturnValue(this.currentField);
			return;
		}

		var fields = this.getOwnedFields();
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

		this.currentField = fields.get(this.minecolonies_tweaks$nextIndex);
		this.minecolonies_tweaks$nextIndex = this.minecolonies_tweaks$nextIndex + 1;

		cir.setReturnValue(this.currentField);
	}

}
