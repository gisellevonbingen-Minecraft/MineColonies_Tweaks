package steve_gall.minecolonies_tweaks.core.common.colony;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;

import net.minecraft.core.BlockPos;

public interface IRegisteredStructureManagerViewExtension
{
	IBuildingExtension minecolonies_tweaks$getBuildingExtension(BlockPos pos);
}
