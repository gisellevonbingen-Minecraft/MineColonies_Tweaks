package steve_gall.minecolonies_tweaks.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class FieldConfig
{
	public final BooleanValue newRetrieveMethod;
	public final IntValue farmMaxRange;

	public FieldConfig(ModConfigSpec.Builder builder)
	{
		builder.comment("If true, Citizen can works same field to without wait time. Also work in field list by sequentially.", "If false, use MineColonies' s method.");
		this.newRetrieveMethod = builder.define("newRetrieveMethod", true);
		this.farmMaxRange = builder.defineInRange("farmMaxRange", 5, 5, 15);
	}

}
