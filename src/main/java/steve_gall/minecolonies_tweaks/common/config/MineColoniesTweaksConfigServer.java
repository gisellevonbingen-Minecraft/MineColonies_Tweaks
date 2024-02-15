package steve_gall.minecolonies_tweaks.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MineColoniesTweaksConfigServer
{
	public static final MineColoniesTweaksConfigServer INSTANCE;
	public static final ForgeConfigSpec SPEC;

	static
	{
		var common = new ForgeConfigSpec.Builder().configure(MineColoniesTweaksConfigServer::new);
		INSTANCE = common.getLeft();
		SPEC = common.getRight();
	}

	public MineColoniesTweaksConfigServer(ForgeConfigSpec.Builder builder)
	{

	}

}
