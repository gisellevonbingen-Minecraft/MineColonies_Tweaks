package steve_gall.minecolonies_tweaks.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class BlockConfig
{
	public final BooleanValue cropVanillaFarmland;
	public final BooleanValue cropIgnoreBiome;
	public final BooleanValue cropCanPerformBonemeal;

	public BlockConfig(ModConfigSpec.Builder builder)
	{
		builder.push("crop");
		builder.comment("Caution: if change this, all planted MineColonies crops when before are will break into item.");
		builder.comment("If this is true, player be can plant crop.");
		builder.comment(ConfigConstants.VANILLA_IS_FALSE);
		this.cropVanillaFarmland = builder.define("vanillaFarmland", true);
		builder.comment(ConfigConstants.VANILLA_IS_FALSE);
		this.cropIgnoreBiome = builder.define("ignoreBiome", true);
		this.cropCanPerformBonemeal = builder.define("canPerformBonemeal", true);
		builder.pop();
	}

}
