package steve_gall.minecolonies_tweaks.core.common.network.message;

import com.ldtteam.blockui.BOScreen;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.core.client.gui.BatchUpgradeBuildingsWindow;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.colony.BatchUpgradeData;
import steve_gall.minecolonies_tweaks.core.common.colony.ColonyExtension;

public class BatchUpgradeDataLoadMessage extends AbstractMessage
{
	private final ResourceKey<Level> dimensionId;
	private final int colonyId;
	private final boolean request;
	private final BatchUpgradeData data;

	public BatchUpgradeDataLoadMessage(IColony colony)
	{
		this.dimensionId = colony.getDimension();
		this.colonyId = colony.getID();
		this.request = false;
		this.data = ((ColonyExtension) colony).minecolonies_tweaks$getBatchUpgrade();
	}

	public BatchUpgradeDataLoadMessage(IColonyView colony)
	{
		this.dimensionId = colony.getDimension();
		this.colonyId = colony.getID();
		this.request = true;
		this.data = new BatchUpgradeData();
	}

	public BatchUpgradeDataLoadMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.dimensionId = buffer.readResourceKey(Registry.DIMENSION_REGISTRY);
		this.colonyId = buffer.readInt();
		this.request = buffer.readBoolean();
		this.data = new BatchUpgradeData();
		this.data.deserializeBuffer(buffer);
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeResourceKey(this.dimensionId);
		buffer.writeInt(this.colonyId);
		buffer.writeBoolean(this.request);
		this.data.serializeBuffer(buffer);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		super.handle(context);

		if (this.isRequest())
		{
			var colony = IColonyManager.getInstance().getColonyByDimension(this.getColonyId(), this.getDimensionId());

			if (colony == null)
			{
				return;
			}

			var response = new BatchUpgradeDataLoadMessage(colony);
			MineColoniesTweaks.network().sendToPlayer(response, context.getSender());
		}
		else
		{
			var mc = Minecraft.getInstance();

			if (mc.screen instanceof BOScreen screen && screen.getWindow() instanceof BatchUpgradeBuildingsWindow window)
			{
				window.setSavedBuildings(this.data);
			}

		}

	}

	public ResourceKey<Level> getDimensionId()
	{
		return this.dimensionId;
	}

	public int getColonyId()
	{
		return this.colonyId;
	}

	public boolean isRequest()
	{
		return this.request;
	}

	public BatchUpgradeData getData()
	{
		return this.data;
	}

}
