package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MCTweaksConfigCommon
{
	public static final MCTweaksConfigCommon INSTANCE;
	public static final ForgeConfigSpec SPEC;

	static
	{
		var common = new ForgeConfigSpec.Builder().configure(MCTweaksConfigCommon::new);
		INSTANCE = common.getLeft();
		SPEC = common.getRight();
	}

	public final BuildingConfig buildings;
	public final WorldGenConfig worldGens;

	public MCTweaksConfigCommon(ForgeConfigSpec.Builder builder)
	{
		builder.push("buildings");
		this.buildings = new BuildingConfig(builder);
		builder.pop();

		builder.push("worldGens");
		this.worldGens = new WorldGenConfig(builder);
		builder.pop();
	}

}
