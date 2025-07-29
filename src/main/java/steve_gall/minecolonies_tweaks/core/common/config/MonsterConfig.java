package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class MonsterConfig
{
	public final BooleanValue disableImmunity;
	public final BooleanValue disableThorns;

	public MonsterConfig(ForgeConfigSpec.Builder builder)
	{
		builder.push("raider");
		builder.comment(ConfigConstants.VANILLA_IS_FALSE);
		this.disableImmunity = builder.define("disableImmunity", true);
		builder.comment(ConfigConstants.VANILLA_IS_FALSE);
		this.disableThorns = builder.define("disableThorns", true);
		builder.pop();
	}

}
