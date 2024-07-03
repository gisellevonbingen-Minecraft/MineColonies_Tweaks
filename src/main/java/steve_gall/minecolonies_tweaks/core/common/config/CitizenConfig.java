package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class CitizenConfig
{
	public final BooleanValue disableMourn;

	public CitizenConfig(ForgeConfigSpec.Builder builder)
	{
		this.disableMourn = builder.define("disableMourn", false);
	}

}
