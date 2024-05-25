package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.views.BOWindow;
import com.minecolonies.core.client.gui.WindowHutAllInventory;

import steve_gall.minecolonies_tweaks.core.client.gui.WindowSkeletonExtension;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;

@Mixin(value = WindowHutAllInventory.class, remap = false)
public abstract class WindowHutAllInventoryMixin implements WindowSkeletonExtension
{
	@Shadow(remap = false)
	private BOWindow prev;

	@Override
	public void minecolonies_tweaks$onCloseHead(CallbackInfo ci)
	{
		if (this.prev != null && MineColoniesTweaksConfigClient.INSTANCE.escToReturn.get().booleanValue())
		{
			ci.cancel();
			this.prev.open();
		}

	}

}
