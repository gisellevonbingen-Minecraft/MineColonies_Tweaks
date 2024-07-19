package steve_gall.minecolonies_tweaks.core.common.network.message;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.core.common.colony.BatchRepairData;
import steve_gall.minecolonies_tweaks.core.common.colony.ColonyExtension;
import steve_gall.minecolonies_tweaks.core.common.network.AbstractMessage;

public class BatchRepairDataSaveMessage extends AbstractMessage
{
	private final ResourceKey<Level> dimensionId;
	private final int colonyId;
	private final BatchRepairData data;

	public BatchRepairDataSaveMessage(IColonyView colony, BatchRepairData data)
	{
		this.dimensionId = colony.getDimension();
		this.colonyId = colony.getID();
		this.data = data;
	}

	public BatchRepairDataSaveMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.dimensionId = buffer.readResourceKey(Registry.DIMENSION_REGISTRY);
		this.colonyId = buffer.readInt();
		this.data = new BatchRepairData();
		this.data.deserializeBuffer(buffer);
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeResourceKey(this.dimensionId);
		buffer.writeInt(this.colonyId);
		this.data.serializeBuffer(buffer);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		super.handle(context);

		var colony = IColonyManager.getInstance().getColonyByDimension(this.getColonyId(), this.getDimensionId());

		if (colony == null)
		{
			return;
		}

		var data = ((ColonyExtension) colony).minecolonies_tweaks$getBatchRepair();
		data.deserializeNBT(this.data.serializeNBT());
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
