package steve_gall.minecolonies_tweaks.core.common.network.message;

import com.ldtteam.blockui.BOScreen;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.core.client.gui.BatchRepairBuildingsWindow;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.colony.BatchRepairData;
import steve_gall.minecolonies_tweaks.core.common.colony.ColonyExtension;

public class BatchRepairDataLoadMessage extends AbstractMessage
{
	public static final CustomPacketPayload.Type<BatchRepairDataLoadMessage> TYPE = new CustomPacketPayload.Type<>(MineColoniesTweaks.rl("batch_repair_data_load"));

	private final ResourceKey<Level> dimensionId;
	private final int colonyId;
	private final boolean request;
	private final BatchRepairData data;

	public BatchRepairDataLoadMessage(IColony colony)
	{
		this.dimensionId = colony.getDimension();
		this.colonyId = colony.getID();
		this.request = false;
		this.data = ((ColonyExtension) colony).minecolonies_tweaks$getBatchRepair();
	}

	public BatchRepairDataLoadMessage(IColonyView colony)
	{
		this.dimensionId = colony.getDimension();
		this.colonyId = colony.getID();
		this.request = true;
		this.data = new BatchRepairData();
	}

	public BatchRepairDataLoadMessage(RegistryFriendlyByteBuf buffer)
	{
		super(buffer);

		this.dimensionId = buffer.readResourceKey(Registries.DIMENSION);
		this.colonyId = buffer.readInt();
		this.request = buffer.readBoolean();
		this.data = new BatchRepairData();
		this.data.deserializeBuffer(buffer);
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeResourceKey(this.dimensionId);
		buffer.writeInt(this.colonyId);
		buffer.writeBoolean(this.request);
		this.data.serializeBuffer(buffer);
	}

	@Override
	public void handle(IPayloadContext context)
	{
		super.handle(context);

		if (this.isRequest())
		{
			var colony = IColonyManager.getInstance().getColonyByDimension(this.getColonyId(), this.getDimensionId());

			if (colony == null)
			{
				return;
			}

			var response = new BatchRepairDataLoadMessage(colony);
			context.reply(response);
		}
		else
		{
			var mc = Minecraft.getInstance();

			if (mc.screen instanceof BOScreen screen && screen.getWindow() instanceof BatchRepairBuildingsWindow window)
			{
				window.setSavedBuildings(this.data);
			}

		}

	}

	@Override
	public CustomPacketPayload.Type<BatchRepairDataLoadMessage> type()
	{
		return TYPE;
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

	public BatchRepairData getData()
	{
		return this.data;
	}

}
