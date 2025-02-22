package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class MonsterConfig
{
	public final BooleanValue disableImmunity;

	public MonsterConfig(ForgeConfigSpec.Builder builder)
	{
		builder.push("raider");
		this.disableImmunity = builder.define("disableImmunity", false);
		builder.pop();
	}

}
