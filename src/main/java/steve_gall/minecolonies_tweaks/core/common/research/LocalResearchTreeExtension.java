package steve_gall.minecolonies_tweaks.core.common.research;

import com.minecolonies.api.colony.IColony;

import net.minecraft.resources.ResourceLocation;

public interface LocalResearchTreeExtension
{
	void minecolonies_tweaks$reset(IColony colony, ResourceLocation branchId);

	void minecolonies_tweaks$resetAll(IColony colony);
}
