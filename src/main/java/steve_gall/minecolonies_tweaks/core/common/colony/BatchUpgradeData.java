package steve_gall.minecolonies_tweaks.core.common.colony;

import java.util.ArrayList;
import java.util.List;

import com.minecolonies.api.util.BlockPosUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class BatchUpgradeData
{
	public static final String TAG_MARK_AS_DONT_UPGRADES = "markAsDontUpgrades";

	private final List<BlockPos> markAsDontUpgrades;

	public BatchUpgradeData()
	{
		this.markAsDontUpgrades = new ArrayList<>();
	}

	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag)
	{
		this.markAsDontUpgrades.clear();
		this.markAsDontUpgrades.addAll(BlockPosUtil.readPosListFromNBT(tag, TAG_MARK_AS_DONT_UPGRADES));
	}

	public CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		var tag = new CompoundTag();
		BlockPosUtil.writePosListToNBT(tag, TAG_MARK_AS_DONT_UPGRADES, this.markAsDontUpgrades);

		return tag;
	}

	public void deserializeBuffer(RegistryFriendlyByteBuf buffer)
	{
		this.markAsDontUpgrades.clear();
		buffer.readList(RegistryFriendlyByteBuf::readBlockPos).forEach(this.markAsDontUpgrades::add);
	}

	public void serializeBuffer(RegistryFriendlyByteBuf buffer)
	{
		buffer.writeCollection(this.markAsDontUpgrades, RegistryFriendlyByteBuf::writeBlockPos);
	}

	public List<BlockPos> getMarkAsDontUpgrades()
	{
		return this.markAsDontUpgrades;
	}

}
