package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class ResearchConfig
{
	public final DoubleValue speed;
	public final BooleanValue ignoreConstraints;

	public ResearchConfig(ForgeConfigSpec.Builder builder)
	{
		this.speed = builder.defineInRange("speed", 1.0D, 0.0D, Integer.MAX_VALUE);
		builder.comment("Can be research multiple 6 depth, ignore limitation of choose only one");
		this.ignoreConstraints = builder.define("ignoreConstraints", false);
	}

}
