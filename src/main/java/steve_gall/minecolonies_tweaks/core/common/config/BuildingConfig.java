package steve_gall.minecolonies_tweaks.core.common.config;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class BuildingConfig
{
	public final ConfigValue<List<? extends String>> customCraftingModules;

	public BuildingConfig(ForgeConfigSpec.Builder builder)
	{
		this.customCraftingModules = builder.defineList("customCraftingModules", Collections.emptyList(), e -> true);
	}

}
