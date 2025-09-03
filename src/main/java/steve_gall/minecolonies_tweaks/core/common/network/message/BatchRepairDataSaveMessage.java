package steve_gall.minecolonies_tweaks.core.common.network.message;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.colony.BatchRepairData;
import steve_gall.minecolonies_tweaks.core.common.colony.ColonyExtension;

public class BatchRepairDataSaveMessage extends AbstractMessage
{
	public static final CustomPacketPayload.Type<BatchRepairDataSaveMessage> TYPE = new CustomPacketPayload.Type<>(MineColoniesTweaks.rl("batch_reapir_data_save"));

	private final ResourceKey<Level> dimensionId;
	private final int colonyId;
	private final BatchRepairData data;

	public BatchRepairDataSaveMessage(IColonyView colony, BatchRepairData data)
	{
		this.dimensionId = colony.getDimension();
		this.colonyId = colony.getID();
		this.data = data;
	}

	public BatchRepairDataSaveMessage(RegistryFriendlyByteBuf buffer)
	{
		super(buffer);

		this.dimensionId = buffer.readResourceKey(Registries.DIMENSION);
		this.colonyId = buffer.readInt();
		this.data = new BatchRepairData();
		this.data.deserializeBuffer(buffer);
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeResourceKey(this.dimensionId);
		buffer.writeInt(this.colonyId);
		this.data.serializeBuffer(buffer);
	}

	@Override
	public void handle(IPayloadContext context)
	{
		super.handle(context);

		var colony = IColonyManager.getInstance().getColonyByDimension(this.getColonyId(), this.getDimensionId());

		if (colony == null)
		{
			return;
		}

		var data = ((ColonyExtension) colony).minecolonies_tweaks$getBatchRepair();
		var registryAccess = colony.getWorld().registryAccess();
		data.deserializeNBT(registryAccess, this.data.serializeNBT(registryAccess));
	}

	@Override
	public CustomPacketPayload.Type<BatchRepairDataSaveMessage> type()
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

	public BatchRepairData getData()
	{
		return this.data;
	}

}
