package steve_gall.minecolonies_tweaks.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class MineColoniesTweaksClient
{
	public MineColoniesTweaksClient()
	{
		IEventBus fml_bus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forge_bus = MinecraftForge.EVENT_BUS;
	}

}
