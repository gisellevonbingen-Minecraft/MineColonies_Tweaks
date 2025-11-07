package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.views.BOWindow;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.client.gui.WindowHutAllInventory;

import net.minecraft.resources.ResourceLocation;

@Mixin(value = WindowHutAllInventory.class, remap = false)
public abstract class WindowHutAllInventoryMixin extends AbstractWindowSkeleton
{
	public WindowHutAllInventoryMixin(ResourceLocation resource)
	{
		super(resource);
	}

	@Shadow(remap = false)
	private BOWindow prev;

	@Inject(method = "back", remap = false, at = @At(value = "INVOKE", target = "Lcom/ldtteam/blockui/views/BOWindow;open()V", remap = false), cancellable = true)
	private void back(CallbackInfo ci)
	{
		if (this.prev == null)
		{
			this.close();
			ci.cancel();
		}

	}

}
