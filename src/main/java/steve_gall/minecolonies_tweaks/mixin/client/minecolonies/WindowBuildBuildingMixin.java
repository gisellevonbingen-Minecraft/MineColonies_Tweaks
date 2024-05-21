package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.client.gui.WindowBuildBuilding;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;

@Mixin(value = WindowBuildBuilding.class, remap = false)
public abstract class WindowBuildBuildingMixin extends AbstractWindowSkeletonMixin
{
	@Shadow(remap = false)
	private IBuildingView building;

	@Override
	protected void close_Head(CallbackInfo ci)
	{
		super.close_Head(ci);

		if (MineColoniesTweaksConfigClient.INSTANCE.escToReturn.get().booleanValue())
		{
			ci.cancel();
			this.building.openGui(false);
		}

	}

}
