package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.core.colony.managers.views.RegisteredStructureManagerView;

import net.minecraft.core.BlockPos;
import steve_gall.minecolonies_tweaks.core.common.colony.IRegisteredStructureManagerViewExtension;

@Mixin(value = RegisteredStructureManagerView.class, remap = false)
public abstract class RegisteredStructureManagerViewMixin implements IRegisteredStructureManagerViewExtension
{
	@Unique
	private final Map<BlockPos, IBuildingExtension> minecolonies_tweaks$pos2buildingExtensions = new HashMap<>();

	@Inject(method = "handleColonyBuildingExtensionViewUpdateMessage", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void handleColonyBuildingExtensionViewUpdateMessage(Set<IBuildingExtension> fields, CallbackInfo ci)
	{
		this.minecolonies_tweaks$pos2buildingExtensions.clear();

		for (var field : fields)
		{
			this.minecolonies_tweaks$pos2buildingExtensions.put(field.getPosition(), field);
		}

	}

	@Override
	public IBuildingExtension minecolonies_tweaks$getBuildingExtension(BlockPos pos)
	{
		return this.minecolonies_tweaks$pos2buildingExtensions.get(pos);
	}

}
