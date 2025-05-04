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
		this.disableImmunity = builder.define("disableImmunity", false);
		this.disableThorns = builder.define("disableThorns", false);
		builder.pop();
	}

}
