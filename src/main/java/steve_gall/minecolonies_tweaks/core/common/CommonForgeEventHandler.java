package steve_gall.minecolonies_tweaks.core.common;

import java.util.HashMap;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import steve_gall.minecolonies_tweaks.api.common.research.ResearchEffectChangedEventArgs;
import steve_gall.minecolonies_tweaks.core.common.research.GlobalResearchEffectExtension;

public class CommonForgeEventHandler
{
	@SubscribeEvent
	public void onResearchEffectChangedEvent(ResearchEffectChangedEventArgs e)
	{
		if (e.getEffect() instanceof GlobalResearchEffectExtension extension)
		{
			var command = extension.minecolonies_tweaks$getCommand();

			if (command != null)
			{
				var logger = MineColoniesTweaks.LOGGER;
				var placeholders = new HashMap<String, String>();
				placeholders.put("effect", e.getEffect().getId().toString());
				placeholders.put("ownerName", e.getColony().getPermissions().getOwnerName());
				placeholders.put("ownerUUID", e.getColony().getPermissions().getOwner().toString());
				placeholders.put("prev", String.valueOf(e.getPrev()));
				placeholders.put("next", String.valueOf(e.getNext()));
				placeholders.put("delta", String.valueOf(e.getNext() - e.getPrev()));

				for (var entry : placeholders.entrySet())
				{
					command = command.replace("<" + entry.getKey() + ">", entry.getValue());
				}

				try
				{
					var server = e.getColony().getWorld().getServer();
					server.getCommands().getDispatcher().execute(command, server.createCommandSourceStack());
					logger.error("ResearchEffectCommand Performed: " + command);
				}
				catch (Exception e1)
				{
					logger.error(e1);
					logger.error("ResearchEffectCommand Error");
					logger.error("Commandline: " + command);
					logger.error("Variables: ");

					for (var entry : placeholders.entrySet())
					{
						logger.error("- " + entry.getKey() + ": " + entry.getValue());
					}

				}

			}

		}

	}

	public CommonForgeEventHandler()
	{

	}

}
