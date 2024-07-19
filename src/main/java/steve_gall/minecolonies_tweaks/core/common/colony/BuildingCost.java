package steve_gall.minecolonies_tweaks.core.common.colony;

import java.util.ArrayList;
import java.util.List;

import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.BlockPosUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public record BuildingCost(BlockPos id, List<ItemStorage> costs)
{

	public static final String TAG_ID = "id";
	public static final String TAG_COSTS = "costs";

	public static BuildingCost deserialize(CompoundTag tag)
	{
		var id = BlockPosUtil.read(tag, TAG_ID);
		var costsTag = tag.getList(TAG_COSTS, Tag.TAG_COMPOUND);
		var costs = new ArrayList<ItemStorage>();

		for (var i = 0; i < costsTag.size(); i++)
		{
			costs.add(StandardFactoryController.getInstance().deserialize(costsTag.getCompound(i)));
		}

		return new BuildingCost(id, costs);
	}

	public static CompoundTag serialize(BuildingCost data)
	{
		var tag = new CompoundTag();
		BlockPosUtil.write(tag, TAG_ID, data.id);
		var costsTag = new ListTag();
		data.costs.stream().map(StandardFactoryController.getInstance()::serialize).forEach(costsTag::add);
		tag.put(TAG_COSTS, costsTag);
		return tag;
	}

	public static BuildingCost decode(FriendlyByteBuf buffer)
	{
		var id = buffer.readBlockPos();
		List<ItemStorage> costs = buffer.readList(StandardFactoryController.getInstance()::deserialize);
		return new BuildingCost(id, costs);
	}

	public static void encode(FriendlyByteBuf buffer, BuildingCost data)
	{
		buffer.writeBlockPos(data.id);
		buffer.writeCollection(data.costs, StandardFactoryController.getInstance()::serialize);
	}

}
