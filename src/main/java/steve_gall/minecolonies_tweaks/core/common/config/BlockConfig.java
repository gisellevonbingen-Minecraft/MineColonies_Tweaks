package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class BlockConfig
{
	public final BooleanValue cropVanillaFarmland;
	public final BooleanValue cropIgnoreBiome;
	public final BooleanValue cropCanPlayerPlant;
	public final BooleanValue cropCanPerformBonemeal;
	public final BooleanValue allowVanillaRandomTicks;

	public BlockConfig(ForgeConfigSpec.Builder builder)
	{
		builder.push("crop");
		builder.comment("Caution: if change this, all planted MineColonies crops when before are will break into item.");
		this.cropVanillaFarmland = builder.define("vanillaFarmland", false);
		this.cropIgnoreBiome = builder.define("ignoreBiome", false);
		this.cropCanPlayerPlant = builder.define("canPlayerPlant", false);
		this.cropCanPerformBonemeal = builder.define("canPerformBonemeal", true);
		this.allowVanillaRandomTicks = builder.define("allowVanillaRandomTicks", false);
		builder.pop();
	}

}
