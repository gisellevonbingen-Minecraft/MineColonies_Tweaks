package steve_gall.minecolonies_tweaks.api.common.building.module;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public abstract class AbstractIdListModule extends AbstractBuildingModule implements IPersistentModule, IIdListModule
{
	public static final String TAG_IDS = MineColoniesTweaks.rl("ids").toString();

	@NotNull
	private final String listId;
	@NotNull
	private final Set<ResourceLocation> ids;

	public AbstractIdListModule(@NotNull String listId)
	{
		this.listId = listId;
		this.ids = new HashSet<>();
	}

	@Override
	public void deserializeNBT(@NotNull CompoundTag compound)
	{
		this.ids.clear();
		var idsTag = compound.getList(TAG_IDS, Tag.TAG_LIST);

		for (var i = 0; i < idsTag.size(); i++)
		{
			this.ids.add(new ResourceLocation(idsTag.getString(i)));
		}

	}

	@Override
	public void serializeNBT(@NotNull CompoundTag compound)
	{
		var idsTag = new ListTag();
		compound.put(TAG_IDS, idsTag);

		for (var id : this.ids)
		{
			idsTag.add(StringTag.valueOf(id.toString()));
		}

	}

	@Override
	public void serializeToView(@NotNull FriendlyByteBuf buf)
	{
		super.serializeToView(buf);

		buf.writeUtf(this.listId);
		buf.writeCollection(this.ids, FriendlyByteBuf::writeResourceLocation);
	}

	@Override
	public void addId(@NotNull ResourceLocation id)
	{
		var added = this.ids.add(id);

		if (added)
		{
			this.markDirty();
		}

	}

	@Override
	public boolean containsId(@NotNull ResourceLocation id)
	{
		return this.ids.contains(id);
	}

	@Override
	public boolean removeId(@NotNull ResourceLocation id)
	{
		var removed = this.ids.remove(id);

		if (removed)
		{
			this.markDirty();
		}

		return removed;
	}

	@Override
	public void clearIds()
	{
		this.ids.clear();
		this.markDirty();
	}

	@Override
	public @NotNull Set<ResourceLocation> getIds()
	{
		return new HashSet<>(this.ids);
	}

	@Override
	public @NotNull String getListId()
	{
		return this.listId;
	}

}
