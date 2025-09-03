package steve_gall.minecolonies_tweaks.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class ResearchConfig
{
	public final DoubleValue speed;
	public final BooleanValue ignoreConstraints;

	public ResearchConfig(ModConfigSpec.Builder builder)
	{
		this.speed = builder.defineInRange("speed", 1.0D, 0.0D, Integer.MAX_VALUE);
		builder.comment("Can be research multiple 6 depth, ignore limitation of choose only one");
		this.ignoreConstraints = builder.define("ignoreConstraints", false);
	}

}
