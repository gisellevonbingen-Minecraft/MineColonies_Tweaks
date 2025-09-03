package steve_gall.minecolonies_tweaks.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class CitizenConfig
{
	public final BooleanValue disableMourn;
	public final BooleanValue disableInteractionDelay;

	public CitizenConfig(ModConfigSpec.Builder builder)
	{
		this.disableMourn = builder.define("disableMourn", false);
		builder.comment(ConfigConstants.VANILLA_IS_FALSE);
		this.disableInteractionDelay = builder.define("disableInteractionDelay", true);
	}

}
