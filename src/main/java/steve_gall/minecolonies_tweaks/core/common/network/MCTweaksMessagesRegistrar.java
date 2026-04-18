package steve_gall.minecolonies_tweaks.core.common.network;

import steve_gall.minecolonies_tweaks.api.common.network.MessageRegistrar;
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
	public static void register(MessageRegistrar channel)
	{
		channel.playToServer(AssignFilterableItemsMessage.TYPE, AssignFilterableItemsMessage::new);
		channel.playToServer(AssignIdListMessage.TYPE, AssignIdListMessage::new);
		channel.playBidirectional(BatchRepairDataLoadMessage.TYPE, BatchRepairDataLoadMessage::new);
		channel.playToServer(BatchRepairDataSaveMessage.TYPE, BatchRepairDataSaveMessage::new);
		channel.playBidirectional(BatchUpgradeDataLoadMessage.TYPE, BatchUpgradeDataLoadMessage::new);
		channel.playToServer(BatchUpgradeDataSaveMessage.TYPE, BatchUpgradeDataSaveMessage::new);
		channel.playBidirectional(ResourcescrollBookOpenMessage.TYPE, ResourcescrollBookOpenMessage::new);
		channel.playToServer(ResearchCostRequestMessage.TYPE, ResearchCostRequestMessage::new);
		channel.playToServer(MaximumStockUpdateMessage.TYPE, MaximumStockUpdateMessage::new);
		channel.playToServer(FarmFieldPlotResize2Message.TYPE, FarmFieldPlotResize2Message::new);
		channel.playToServer(CopyScrollRemoveEntryMessage.TYPE, CopyScrollRemoveEntryMessage::new);
	}

	private MCTweaksMessagesRegistrar()
	{

	}

}
