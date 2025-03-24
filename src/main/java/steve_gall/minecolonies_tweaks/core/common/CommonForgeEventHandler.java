package steve_gall.minecolonies_tweaks.core.common;

import java.util.HashMap;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.util.constant.TranslationConstants;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import steve_gall.minecolonies_tweaks.api.common.research.ResearchEffectChangedEventArgs;
import steve_gall.minecolonies_tweaks.core.common.colony.ColonyExtension;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksTags;
import steve_gall.minecolonies_tweaks.core.common.research.GlobalResearchEffectExtension;

public class CommonForgeEventHandler
{
	public static final Component GREAT_FOOD_TOOLTIP = Component.translatable(TranslationConstants.TIER_TOOLTIP + 3);
	public static final Component DECENT_FOOD_TOOLTIP = Component.translatable(TranslationConstants.TIER_TOOLTIP + 2);
	public static final Component FINE_FOOD_TOOLTIP = Component.translatable(TranslationConstants.TIER_TOOLTIP + 1);

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent e)
	{
		var tier = MCTweaksTags.Items.getFoodTier(e.getItemStack()::is);

		if (tier == 3)
		{
			e.getToolTip().add(1, GREAT_FOOD_TOOLTIP);
		}
		else if (tier == 2)
		{
			e.getToolTip().add(1, DECENT_FOOD_TOOLTIP);
		}
		else if (tier == 1)
		{
			e.getToolTip().add(1, FINE_FOOD_TOOLTIP);
		}

	}

	@SubscribeEvent
	public void onResearchEffectChangedEvent(ResearchEffectChangedEventArgs e)
	{
		if (e.getNext() > e.getPrev() && e.getEffect() instanceof GlobalResearchEffectExtension extension)
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
		if (e.getEntity() instanceof ServerPlayer player && IColonyManager.getInstance().getIColonyByOwner(player.level(), player) instanceof ColonyExtension extension)
		{
			var queue = extension.minecolonies_tweaks$getCommandQueue();
			var server = player.level().getServer();

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
