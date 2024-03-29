package steve_gall.minecolonies_tweaks.common.config;

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

	public MineColoniesTweaksConfigCommon(ForgeConfigSpec.Builder builder)
	{
		builder.push("tools");
		this.tools = new ToolConfig(builder);
		builder.pop();
	}

}
