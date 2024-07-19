package steve_gall.minecolonies_tweaks.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MineColoniesTweaksConfigCommon
{
	public static final MineColoniesTweaksConfigCommon INSTANCE;
	public static final ForgeConfigSpec SPEC;

	static
	{
		var common = new ForgeConfigSpec.Builder().configure(MineColoniesTweaksConfigCommon::new);
		INSTANCE = common.getLeft();
		SPEC = common.getRight();
	}

	public final ToolConfig tools;
	public final BuildingConfig buildings;
	public final WorldGenConfig worldGens;

	public MineColoniesTweaksConfigCommon(ForgeConfigSpec.Builder builder)
	{
		builder.push("tools");
		this.tools = new ToolConfig(builder);
		builder.pop();

		builder.push("buildings");
		this.buildings = new BuildingConfig(builder);
		builder.pop();

		builder.push("worldGens");
		this.worldGens = new WorldGenConfig(builder);
		builder.pop();
	}

}
