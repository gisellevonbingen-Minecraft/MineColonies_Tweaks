package steve_gall.minecolonies_tweaks.core.client;

import com.ldtteam.blockui.BOScreen;
import com.ldtteam.blockui.Loader;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.items.ItemResourceScroll;

import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElementEvent;
import steve_gall.minecolonies_tweaks.core.client.gui.AbstractContainerScreenExtension;
import steve_gall.minecolonies_tweaks.core.client.gui.ClipboardElement;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableWindowExtension;
import steve_gall.minecolonies_tweaks.core.client.gui.ColonyMapElement;
import steve_gall.minecolonies_tweaks.core.client.gui.InventoryScrollElement;
import steve_gall.minecolonies_tweaks.core.client.gui.QuestLogElement;
import steve_gall.minecolonies_tweaks.core.client.gui.ResourceScrollBookInventoryScreen;
import steve_gall.minecolonies_tweaks.core.client.gui.ResourceScrollElement;
import steve_gall.minecolonies_tweaks.core.client.gui.UniversityScrollElement;
import steve_gall.minecolonies_tweaks.core.client.view.Addition;
import steve_gall.minecolonies_tweaks.core.client.view.FluidIcon;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksItems;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksMenuTypes;
import steve_gall.minecolonies_tweaks.core.common.item.ItemInventoryScroll;
import steve_gall.minecolonies_tweaks.core.common.network.message.ResourcescrollBookOpenMessage;

public class MineColoniesTweaksClient
{
	public MineColoniesTweaksClient(FMLModContainer modContainer)
	{
		var fml_bus = modContainer.getEventBus();
		fml_bus.addListener(this::onRegisterMenuScreens);
		fml_bus.addListener(this::onRegisterKeyMappings);

		var forge_bus = NeoForge.EVENT_BUS;
		forge_bus.addListener(this::onScreenInitPost);
		forge_bus.addListener(this::onScreenOpening);
		forge_bus.addListener(this::onClientTickEvent);
		forge_bus.addListener(this::onResourceScrollBookElement);

		Loader.INSTANCE.register(MineColoniesTweaks.rl("addition").toString(), Addition::new);
		Loader.INSTANCE.register(MineColoniesTweaks.rl("fluidicon").toString(), FluidIcon::new);
	}

	private void onRegisterMenuScreens(RegisterMenuScreensEvent e)
	{
		e.register(MCTweaksMenuTypes.RESOURCESCROLL_BOOK_INVENTORY.get(), ResourceScrollBookInventoryScreen::new);
	}

	private void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
	{
		ModKeyMappings.register(event);
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
				if (event.getCurrentScreen() instanceof BOScreen screen)
				{
					extension.minecolonies_tweaks$setParent(screen);
				}

			}
		});

	}

	private void onClientTickEvent(ClientTickEvent.Pre event)
	{
		if (ModKeyMappings.RESOURCESCROLL_BOOK.get().consumeClick())
		{
			PacketDistributor.sendToServer(new ResourcescrollBookOpenMessage());
		}

	}

	private void onResourceScrollBookElement(ResourceScrollBookElementEvent event)
	{
		var stack = event.getStack();

		if (stack.getItem() instanceof ItemResourceScroll)
		{
			event.register(new ResourceScrollElement(stack));
		}
		else if (stack.is(ModItems.clipboard))
		{
			event.register(new ClipboardElement(stack));
		}
		else if (stack.is(ModItems.colonyMap))
		{
			event.register(new ColonyMapElement(stack));
		}
		else if (stack.is(ModItems.questLog))
		{
			event.register(new QuestLogElement(stack));
		}
		else if (stack.getItem() instanceof ItemInventoryScroll)
		{
			event.register(new InventoryScrollElement(stack));
		}
		else if (stack.is(MCTweaksItems.UNIVERSITYSCROLL.get()))
		{
			event.register(new UniversityScrollElement(stack));
		}

	}

}
