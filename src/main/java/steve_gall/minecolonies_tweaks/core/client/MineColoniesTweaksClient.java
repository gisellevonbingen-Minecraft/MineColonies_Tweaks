package steve_gall.minecolonies_tweaks.core.client;

import com.ldtteam.blockui.Loader;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import steve_gall.minecolonies_tweaks.core.client.gui.AbstractContainerScreenExtension;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableWindowExtension;
import steve_gall.minecolonies_tweaks.core.client.view.Addition;
import steve_gall.minecolonies_tweaks.core.client.view.FluidIcon;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class MineColoniesTweaksClient
{
	public MineColoniesTweaksClient()
	{
		var fml_bus = FMLJavaModLoadingContext.get().getModEventBus();
		var forge_bus = MinecraftForge.EVENT_BUS;
		forge_bus.addListener(this::onScreenInitPost);
		forge_bus.addListener(this::onScreenOpening);

		Loader.INSTANCE.register(MineColoniesTweaks.rl("addition").toString(), Addition::new);
		Loader.INSTANCE.register(MineColoniesTweaks.rl("fluidicon").toString(), FluidIcon::new);
	}

	private void onScreenInitPost(ScreenEvent.Init.Post event)
	{
		if (event.getScreen() instanceof AbstractContainerScreenExtension extension)
		{
			extension.minecolonies_tweaks$onInitPost();
		}

	}

	private void onScreenOpening(ScreenEvent.Opening event)
	{
		CloseableWindowExtension.find(event.getNewScreen()).ifPresent(extension ->
		{
			if (extension.minecolonies_tweaks$getParent() == null)
			{
				extension.minecolonies_tweaks$setParent(event.getCurrentScreen());
			}
		});

	}

}
