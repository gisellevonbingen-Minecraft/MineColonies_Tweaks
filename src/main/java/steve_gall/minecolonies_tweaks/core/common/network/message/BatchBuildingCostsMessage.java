package steve_gall.minecolonies_tweaks.core.common.network.message;

import java.util.ArrayList;
import java.util.List;

import com.minecolonies.api.colony.IColony;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import steve_gall.minecolonies_tweaks.core.common.colony.BuildingCost;
import steve_gall.minecolonies_tweaks.core.common.network.AbstractMessage;

public abstract class BatchBuildingCostsMessage extends AbstractMessage
{
	private final ResourceKey<Level> dimensionId;
	private final int colonyId;
	private final List<BuildingCost> buildings;

	public BatchBuildingCostsMessage(IColony colony)
	{
		this.dimensionId = colony.getDimension();
		this.colonyId = colony.getID();
		this.buildings = new ArrayList<>();
	}

	public BatchBuildingCostsMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.dimensionId = buffer.readResourceKey(Registries.DIMENSION);
		this.colonyId = buffer.readInt();
		this.buildings = buffer.readCollection(ArrayList::new, BuildingCost::decode);
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeResourceKey(this.dimensionId);
		buffer.writeInt(this.colonyId);
		buffer.writeCollection(this.buildings, BuildingCost::encode);
	}

	public ResourceKey<Level> getDimensionId()
	{
		return this.dimensionId;
	}

	public int getColonyId()
	{
		return this.colonyId;
	}

	public List<BuildingCost> getBuildings()
	{
		return this.buildings;
	}

}
