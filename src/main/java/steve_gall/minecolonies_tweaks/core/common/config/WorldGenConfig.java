package steve_gall.minecolonies_tweaks.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class WorldGenConfig
{
	public final DoubleValue emptyColoniesGenerationChance;

	public WorldGenConfig(ModConfigSpec.Builder builder)
	{
		this.emptyColoniesGenerationChance = builder.defineInRange("emptyColoniesGenerationChance", 1.0D, 0.0D, 1.0D);
	}

}
