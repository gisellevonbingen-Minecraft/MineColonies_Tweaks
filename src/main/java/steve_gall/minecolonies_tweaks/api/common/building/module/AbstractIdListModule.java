package steve_gall.minecolonies_tweaks.api.common.building.module;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public abstract class AbstractIdListModule extends AbstractBuildingModule implements ICopyableModule, IIdListModule
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
		var idsTag = compound.getList(TAG_IDS, Tag.TAG_STRING);

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

	protected void onIdAdded(@NotNull ResourceLocation id)
	{

	}

	protected void onIdRemoved(@NotNull ResourceLocation id)
	{

	}

	protected void onIdsCleared()
	{

	}

	protected void onIdsChanged()
	{

	}

	@Override
	public boolean addId(@NotNull ResourceLocation id)
	{
		var added = this.ids.add(id);

		if (added)
		{
			this.onIdAdded(id);
			this.onIdsChanged();
			this.markDirty();
		}

		return added;
	}

	@Override
	public boolean addIds(@NotNull Collection<ResourceLocation> ids)
	{
		var anyAdded = false;

		for (var id : ids)
		{
			if (this.ids.add(id))
			{
				anyAdded = true;
				this.onIdAdded(id);
			}

		}

		if (anyAdded)
		{
			this.onIdsChanged();
			this.markDirty();
		}

		return anyAdded;
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
			this.onIdRemoved(id);
			this.onIdsChanged();
			this.markDirty();
		}

		return removed;
	}

	@Override
	public boolean removeIds(@NotNull Collection<ResourceLocation> ids)
	{
		var anyRemoved = false;

		for (var id : ids)
		{
			if (this.ids.remove(id))
			{
				anyRemoved = true;
				this.onIdRemoved(id);
			}

		}

		if (anyRemoved)
		{
			this.onIdsChanged();
			this.markDirty();
		}

		return false;
	}

	@Override
	public void clearIds()
	{
		this.ids.clear();

		this.onIdsCleared();
		this.onIdsChanged();
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
