package steve_gall.minecolonies_tweaks.api.common.building;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.util.BlockPosUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class BuildingPos
{
	public static final String TAG_DIMENSION_ID = "dimensionId";
	public static final String TAG_COLONY_ID = "colonyId";
	public static final String TAG_BUILDING_ID = "buildingId";

	@NotNull
	private final ResourceKey<Level> dimensionId;
	private final int colonyId;
	@NotNull
	private final BlockPos buildingId;

	public BuildingPos(@NotNull CompoundTag tag)
	{
		this.dimensionId = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString(TAG_DIMENSION_ID)));
		this.colonyId = tag.getInt(TAG_COLONY_ID);
		this.buildingId = BlockPosUtil.read(tag, TAG_BUILDING_ID);
	}

	public BuildingPos(@NotNull FriendlyByteBuf buffer)
	{
		this.dimensionId = buffer.readResourceKey(Registry.DIMENSION_REGISTRY);
		this.colonyId = buffer.readInt();
		this.buildingId = buffer.readBlockPos();
	}

	public BuildingPos(@NotNull ResourceKey<Level> dimensionId, int colonyId, @NotNull BlockPos buildingId)
	{
		this.dimensionId = dimensionId;
		this.colonyId = colonyId;
		this.buildingId = buildingId;
	}

	public BuildingPos(@NotNull IColony colony, @NotNull BlockPos buildingId)
	{
		this(colony.getDimension(), colony.getID(), buildingId);
	}

	public BuildingPos(@NotNull IBuilding building)
	{
		this(building.getColony(), building.getID());
	}

	public BuildingPos(@NotNull IBuildingView buildingView)
	{
		this(buildingView.getColony(), buildingView.getID());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.dimensionId, this.colonyId, this.buildingId);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj instanceof BuildingPos other)
		{
			return this.dimensionId == other.dimensionId //
					&& this.colonyId == other.colonyId//
					&& this.buildingId.equals(other.buildingId);
		}
		else
		{
			return false;
		}

	}

	@Nullable
	public IBuilding getBuilding()
	{
		var colony = IColonyManager.getInstance().getColonyByDimension(this.colonyId, this.dimensionId);

		if (colony == null)
		{
			return null;
		}

		return colony.getBuildingManager().getBuilding(this.buildingId);
	}

	@Nullable
	public IBuildingView getBuildingView()
	{
		var colony = IColonyManager.getInstance().getColonyView(this.colonyId, this.dimensionId);

		if (colony == null)
		{
			return null;
		}

		return colony.getBuilding(this.buildingId);
	}

	@NotNull
	public CompoundTag serializeNBT()
	{
		var tag = new CompoundTag();
		tag.putString(TAG_DIMENSION_ID, this.dimensionId.location().toString());
		tag.putInt(TAG_COLONY_ID, this.colonyId);
		BlockPosUtil.write(tag, TAG_BUILDING_ID, this.buildingId);

		return tag;
	}

	public void serializeBuffer(@NotNull FriendlyByteBuf buffer)
	{
		buffer.writeResourceKey(this.dimensionId);
		buffer.writeInt(this.colonyId);
		buffer.writeBlockPos(this.buildingId);
	}

	@NotNull
	public ResourceKey<Level> getDimensionId()
	{
		return this.dimensionId;
	}

	public int getColonyId()
	{
		return colonyId;
	}

	@NotNull
	public BlockPos getBuildingId()
	{
		return this.buildingId;
	}

	public int getX()
	{
		return this.getBuildingId().getX();
	}

	public int getY()
	{
		return this.getBuildingId().getY();
	}

	public int getZ()
	{
		return this.getBuildingId().getZ();
	}

}
