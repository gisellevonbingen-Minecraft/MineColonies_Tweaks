package steve_gall.minecolonies_tweaks.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class ItemConfig
{
	public final BooleanValue disableFoodTierEatingTimeMultiplier;

	public ItemConfig(ModConfigSpec.Builder builder)
	{
		builder.comment("Use the base eating time for MineColonies food without the food tier multiplier.");
		builder.comment(ConfigConstants.VANILLA_IS_FALSE);
		this.disableFoodTierEatingTimeMultiplier = builder.define("disableFoodTierEatingTimeMultiplier", true);
	}

}
