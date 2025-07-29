package steve_gall.minecolonies_tweaks.core.common.config;

public class ConfigConstants
{
	public static final String VANILLA_IS_FALSE = VANILLA_IS(false);

	public static <T> String VANILLA_IS(T value)
	{
		return "MineColonies's default value is `" + value + "`";
	}

	private ConfigConstants()
	{

	}

}
