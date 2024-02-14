package steve_gall.minecolonies_tweaks.common.config;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ToolTypeConfig
{
	public final ConfigValue<List<? extends String>> add;

	public ToolTypeConfig(ForgeConfigSpec.Builder builder)
	{
		this.add = builder.defineList("add", Collections.emptyList(), e -> true);
	}

}
