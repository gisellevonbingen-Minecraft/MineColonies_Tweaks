package steve_gall.minecolonies_tweaks.mixin.client.blockui;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.UiRenderMacros;
import com.ldtteam.blockui.controls.TextField;

@Mixin(value = Pane.class, remap = false)
public abstract class PaneMixin extends UiRenderMacros
{
	@Unique
	private Optional<TextField> minecolonies_tweaks$textField = null;

	@Inject(method = "handleRightClick", remap = false, at = @At(value = "HEAD"))
	private void handleRightClick(double mx, double my, CallbackInfoReturnable<Boolean> cir)
	{
		if (this.minecolonies_tweaks$textField == null)
		{
			this.minecolonies_tweaks$textField = ((Object) this) instanceof TextField textField ? Optional.of(textField) : Optional.empty();
		}

		this.minecolonies_tweaks$textField.ifPresent(textField ->
		{
			textField.setFocus();
			textField.setText("");
			textField.writeText("");
		});

	}

}
