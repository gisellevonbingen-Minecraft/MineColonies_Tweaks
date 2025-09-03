package steve_gall.minecolonies_tweaks.core.common.config;

import java.util.Collections;
import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class BuildingConfig
{
	public final ConfigValue<List<? extends String>> customCraftingModules;

	public BuildingConfig(ModConfigSpec.Builder builder)
	{
		this.customCraftingModules = builder.defineList("customCraftingModules", Collections::emptyList, () -> "", e -> true);
	}

}
