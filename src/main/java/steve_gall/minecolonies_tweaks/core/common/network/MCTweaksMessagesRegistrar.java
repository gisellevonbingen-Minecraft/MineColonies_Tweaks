package steve_gall.minecolonies_tweaks.core.common.network;

import steve_gall.minecolonies_tweaks.api.common.network.NetworkChannel;
import steve_gall.minecolonies_tweaks.core.common.network.message.AssignFilterableItemsMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.AssignIdListMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.BatchRepairDataLoadMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.BatchRepairDataSaveMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.BatchUpgradeDataLoadMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.BatchUpgradeDataSaveMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.CopyScrollRemoveEntryMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.FarmFieldPlotResize2Message;
import steve_gall.minecolonies_tweaks.core.common.network.message.MaximumStockUpdateMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.ResearchCostRequestMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.ResourcescrollBookOpenMessage;

public class MCTweaksMessagesRegistrar
{
	public static void register(NetworkChannel channel)
	{
		channel.registerMessage(AssignFilterableItemsMessage.class, AssignFilterableItemsMessage::new);
		channel.registerMessage(AssignIdListMessage.class, AssignIdListMessage::new);
		channel.registerMessage(BatchRepairDataLoadMessage.class, BatchRepairDataLoadMessage::new);
		channel.registerMessage(BatchRepairDataSaveMessage.class, BatchRepairDataSaveMessage::new);
		channel.registerMessage(BatchUpgradeDataLoadMessage.class, BatchUpgradeDataLoadMessage::new);
		channel.registerMessage(BatchUpgradeDataSaveMessage.class, BatchUpgradeDataSaveMessage::new);
		channel.registerMessage(ResourcescrollBookOpenMessage.class, ResourcescrollBookOpenMessage::new);
		channel.registerMessage(ResearchCostRequestMessage.class, ResearchCostRequestMessage::new);
		channel.registerMessage(MaximumStockUpdateMessage.class, MaximumStockUpdateMessage::new);
		channel.registerMessage(FarmFieldPlotResize2Message.class, FarmFieldPlotResize2Message::new);
		channel.registerMessage(CopyScrollRemoveEntryMessage.class, CopyScrollRemoveEntryMessage::new);
	}

	private MCTweaksMessagesRegistrar()
	{

	}

}
