package steve_gall.minecolonies_tweaks.core.common.colony;

import java.util.ArrayList;

public interface ColonyExtension
{
	BatchRepairData minecolonies_tweaks$getBatchRepair();

	BatchUpgradeData minecolonies_tweaks$getBatchUpgrade();

	ArrayList<String> minecolonies_tweaks$getCommandQueue();
}
