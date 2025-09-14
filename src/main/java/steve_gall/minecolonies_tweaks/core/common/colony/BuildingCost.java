package steve_gall.minecolonies_tweaks.core.common.colony;

import java.util.List;

import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.NBTUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import steve_gall.minecolonies_tweaks.core.common.util.SerializationHelper;

public record BuildingCost(BlockPos id, List<ItemStorage> costs)
{

	public static final String TAG_ID = "id";
	public static final String TAG_COSTS = "costs";

	public static BuildingCost deserialize(CompoundTag tag)
	{
		var id = BlockPosUtil.read(tag, TAG_ID);
		var costs = NBTUtils.streamCompound(tag.getList(TAG_COSTS, Tag.TAG_COMPOUND)).map(SerializationHelper.<ItemStorage> deserializerTag()).toList();
		return new BuildingCost(id, costs);
	}

	public static CompoundTag serialize(BuildingCost data)
	{
		var tag = new CompoundTag();
		BlockPosUtil.write(tag, TAG_ID, data.id);
		tag.put(TAG_COSTS, data.costs.stream().map(SerializationHelper.serializerTag()).collect(NBTUtils.toListNBT()));
		return tag;
	}

	public static BuildingCost decode(FriendlyByteBuf buffer)
	{
		var id = buffer.readBlockPos();
		var costs = buffer.readList(SerializationHelper.<ItemStorage> deserializer());
		return new BuildingCost(id, costs);
	}

	public static void encode(FriendlyByteBuf buffer, BuildingCost data)
	{
		buffer.writeBlockPos(data.id);
		buffer.writeCollection(data.costs, SerializationHelper.serializer());
	}

}
