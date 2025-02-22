package steve_gall.minecolonies_tweaks.core.common.config;

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

	public final ResearchConfig researches;
	public final FieldConfig fields;
	public final JobConfig jobs;
	public final CitizenConfig citizens;
	public final MonsterConfig monsters;
	public final BlockConfig blocks;

	public MineColoniesTweaksConfigServer(ForgeConfigSpec.Builder builder)
	{
		builder.push("researches");
		this.researches = new ResearchConfig(builder);
		builder.pop();

		builder.push("fields");
		this.fields = new FieldConfig(builder);
		builder.pop();

		builder.push("jobs");
		this.jobs = new JobConfig(builder);
		builder.pop();

		builder.push("citizens");
		this.citizens = new CitizenConfig(builder);
		builder.pop();

		builder.push("monsters");
		this.monsters = new MonsterConfig(builder);
		builder.pop();

		builder.push("blocks");
		this.blocks = new BlockConfig(builder);
		builder.pop();
	}

}
