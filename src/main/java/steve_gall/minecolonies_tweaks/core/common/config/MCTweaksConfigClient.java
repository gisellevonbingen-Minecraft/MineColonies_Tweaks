package steve_gall.minecolonies_tweaks.core.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class MCTweaksConfigClient
{
	public static final MCTweaksConfigClient INSTANCE;
	public static final ModConfigSpec SPEC;

	static
	{
		var common = new ModConfigSpec.Builder().configure(MCTweaksConfigClient::new);
		INSTANCE = common.getLeft();
		SPEC = common.getRight();
	}

	public final BooleanValue escToReturn;
	public final BooleanValue addReturnButton;
	public final BooleanValue renderFieldSeed;

	public MCTweaksConfigClient(ModConfigSpec.Builder builder)
	{
		builder.push("gui");
		builder.comment("ESC key allows return to previous window.", "Applies at Hut/Citizen inventory and Recipe Teach window and Hire window.");
		this.escToReturn = builder.define("escToReturn", true);
		builder.comment("Add close button what can return previous window.", "Applies at Hut/Citizen inventory and Recipe Teach window.");
		this.addReturnButton = builder.define("addCloseButton", false);
		builder.pop();

		builder.push("fields");
		builder.comment("Render selected seed item above field's head.");
		this.renderFieldSeed = builder.define("renderSeed", true);
		builder.pop();
	}

}
