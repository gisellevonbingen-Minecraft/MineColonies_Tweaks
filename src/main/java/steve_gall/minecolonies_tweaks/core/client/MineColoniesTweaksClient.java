package steve_gall.minecolonies_tweaks.core.client;

import com.ldtteam.blockui.Loader;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import steve_gall.minecolonies_tweaks.core.client.view.Addition;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class MineColoniesTweaksClient
{
	public MineColoniesTweaksClient()
	{
		var fml_bus = FMLJavaModLoadingContext.get().getModEventBus();
		var forge_bus = MinecraftForge.EVENT_BUS;

		Loader.INSTANCE.register(MineColoniesTweaks.rl("addition").toString(), Addition::new);
	}

}
