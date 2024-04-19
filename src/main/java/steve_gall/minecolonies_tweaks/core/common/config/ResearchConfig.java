package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class ResearchConfig
{
	public final DoubleValue speed;

	public ResearchConfig(ForgeConfigSpec.Builder builder)
	{
		this.speed = builder.defineInRange("speed", 1.0D, 0.0D, Integer.MAX_VALUE);
	}

}
