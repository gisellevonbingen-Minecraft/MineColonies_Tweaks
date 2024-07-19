package steve_gall.minecolonies_tweaks.core.common.colony;

import java.util.ArrayList;
import java.util.List;

import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.NBTUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public class BatchRepairData
{
	public static final String TAG_COSTS = "costs";
	public static final String TAG_MARK_AS_DONT_REPAIRS = "markAsDontRepairs";

	private final List<BuildingCost> costs;
	private final List<BlockPos> markAsDontRepairs;

	public BatchRepairData()
	{
		this.costs = new ArrayList<>();
		this.markAsDontRepairs = new ArrayList<>();
	}

	public void deserializeNBT(CompoundTag tag)
	{
		this.costs.clear();
		NBTUtils.streamCompound(tag.getList(TAG_COSTS, Tag.TAG_COMPOUND)).map(BuildingCost::deserialize).forEach(this.costs::add);

		this.markAsDontRepairs.clear();
		this.markAsDontRepairs.addAll(BlockPosUtil.readPosListFromNBT(tag, TAG_MARK_AS_DONT_REPAIRS));
	}

	public CompoundTag serializeNBT()
	{
		var tag = new CompoundTag();
		tag.put(TAG_COSTS, this.costs.stream().map(BuildingCost::serialize).collect(NBTUtils.toListNBT()));
		BlockPosUtil.writePosListToNBT(tag, TAG_MARK_AS_DONT_REPAIRS, this.markAsDontRepairs);

		return tag;
	}

	public void deserializeBuffer(FriendlyByteBuf buffer)
	{
		this.costs.clear();
		buffer.readList(BuildingCost::decode).forEach(this.costs::add);

		this.markAsDontRepairs.clear();
		buffer.readList(FriendlyByteBuf::readBlockPos).forEach(this.markAsDontRepairs::add);
	}

	public void serializeBuffer(FriendlyByteBuf buffer)
	{
		buffer.writeCollection(this.costs, BuildingCost::encode);
		buffer.writeCollection(this.markAsDontRepairs, FriendlyByteBuf::writeBlockPos);
	}

	public List<BuildingCost> getCosts()
	{
		return this.costs;
	}

	public List<BlockPos> getMarkAsDontRepairs()
	{
		return this.markAsDontRepairs;
	}

}
