package steve_gall.minecolonies_tweaks.core.common;

import java.util.HashMap;

import com.minecolonies.api.colony.IColonyManager;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import steve_gall.minecolonies_tweaks.api.common.research.ResearchEffectChangedEventArgs;
import steve_gall.minecolonies_tweaks.core.common.colony.ColonyExtension;
import steve_gall.minecolonies_tweaks.core.common.research.GlobalResearchEffectExtension;

public class CommonForgeEventHandler
{
	@SubscribeEvent
	public void onResearchEffectChangedEvent(ResearchEffectChangedEventArgs e)
	{
		if (e.getEffect() instanceof GlobalResearchEffectExtension extension)
		{
			var command = extension.minecolonies_tweaks$getCommand();

			if (command == null)
			{
				return;
			}

			var colony = e.getColony();
			command = this.patchCommand(e, command);

			var server = colony.getWorld().getServer();
			var owner = server.getPlayerList().getPlayer(colony.getPermissions().getOwner());

			if (owner != null || extension.minecolonies_tweaks$isOfflineRunnable())
			{
				this.performCommand(server, command);
			}
			else
			{
				((ColonyExtension) colony).minecolonies_tweaks$getCommandQueue().add(command);
				MineColoniesTweaks.LOGGER.info("ResearchEffectCommand Enqueued: " + command);
			}

		}

	}

	private String patchCommand(ResearchEffectChangedEventArgs e, String command)
	{
		var colony = e.getColony();
		var placeholders = new HashMap<String, String>();
		placeholders.put("effect", e.getEffect().getId().toString());
		placeholders.put("ownerName", colony.getPermissions().getOwnerName());
		placeholders.put("ownerUUID", colony.getPermissions().getOwner().toString());
		placeholders.put("prev", String.valueOf(e.getPrev()));
		placeholders.put("next", String.valueOf(e.getNext()));
		placeholders.put("delta", String.valueOf(e.getNext() - e.getPrev()));

		for (var entry : placeholders.entrySet())
		{
			command = command.replace("<" + entry.getKey() + ">", entry.getValue());
		}

		return command;
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
	{
		if (e.getEntity() instanceof ServerPlayer player && IColonyManager.getInstance().getIColonyByOwner(player.level, player) instanceof ColonyExtension extension)
		{
			var queue = extension.minecolonies_tweaks$getCommandQueue();
			var server = player.level.getServer();

			for (var command : queue)
			{
				this.performCommand(server, command);
			}

			queue.clear();
		}

	}

	private boolean performCommand(MinecraftServer server, String command)
	{
		var logger = MineColoniesTweaks.LOGGER;

		try
		{
			server.getCommands().getDispatcher().execute(command, server.createCommandSourceStack());
			logger.info("ResearchEffectCommand Performed: " + command);
			return true;
		}
		catch (Exception ex)
		{
			logger.error(ex);
			logger.error("ResearchEffectCommand Error");
			logger.error("Commandline: " + command);
			return false;
		}

	}

	public CommonForgeEventHandler()
	{

	}

}
