package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class FieldConfig
{
	public final IntValue farmMaxRange;

	public FieldConfig(ForgeConfigSpec.Builder builder)
	{
		this.farmMaxRange = builder.defineInRange("farmMaxRange", 5, 5, 15);
	}

}
