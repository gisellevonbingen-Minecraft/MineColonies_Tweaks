package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class CitizenConfig
{
	public final BooleanValue disableMourn;
	public final BooleanValue disableInteractionDelay;

	public CitizenConfig(ForgeConfigSpec.Builder builder)
	{
		this.disableMourn = builder.define("disableMourn", false);
		builder.comment("MineColonies's default value is false");
		this.disableInteractionDelay = builder.define("disableInteractionDelay", true);
	}

}
