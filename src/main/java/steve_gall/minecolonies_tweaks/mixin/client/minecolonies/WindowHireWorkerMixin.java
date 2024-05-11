package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.core.client.gui.WindowHireWorker;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;

import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;

@Mixin(value = WindowHireWorker.class, remap = false)
public abstract class WindowHireWorkerMixin extends AbstractWindowSkeletonMixin
{
	@Shadow(remap = false)
	protected AbstractBuildingView building;

	@Shadow(remap = false)
	protected IColonyView colony;

	@Override
	protected void close_Head(CallbackInfo ci)
	{
		super.close_Head(ci);

		if (this.colony.getTownHall() != null && MineColoniesTweaksConfigClient.INSTANCE.escToReturn.get().booleanValue())
		{
			ci.cancel();
			this.building.openGui(false);
		}

	}

}
