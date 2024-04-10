package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class WorldGenConfig
{
	public final DoubleValue emptyColoniesGenerationChance;

	public WorldGenConfig(ForgeConfigSpec.Builder builder)
	{
		this.emptyColoniesGenerationChance = builder.defineInRange("emptyColoniesGenerationChance", 1.0D, 0.0D, 1.0D);
	}

}
