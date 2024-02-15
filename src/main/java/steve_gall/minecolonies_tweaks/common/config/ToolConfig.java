package steve_gall.minecolonies_tweaks.common.config;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ToolConfig
{
	public final ConfigValue<List<? extends String>> customTypes;

	public ToolConfig(ForgeConfigSpec.Builder builder)
	{
		this.customTypes = builder.defineList("customTypes", Collections.emptyList(), e -> true);
	}

}
