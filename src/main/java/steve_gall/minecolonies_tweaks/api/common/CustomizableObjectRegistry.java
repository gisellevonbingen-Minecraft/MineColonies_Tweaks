package steve_gall.minecolonies_tweaks.api.common;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public abstract class CustomizableObjectRegistry<OBJECT, ENTRY extends CustomizableObjectRegistry.Entry>
{
	public static final String TAG_ID = "ID";
	public static final String TAG_OBJECT = "Object";

	public static final ResourceLocation EMPTY_ID = MineColoniesTweaks.rl("empty");
	public static final String EMPTY_ID_STRING = EMPTY_ID.toString();

	private final Map<ResourceLocation, ENTRY> map = new HashMap<>();

	public CustomizableObjectRegistry()
	{

	}

	protected void register(@NotNull ENTRY entry)
	{
		var id = entry.getId();

		if (this.map.containsKey(id))
		{
			throw new IllegalArgumentException("ID " + id + " is already registered");
		}

		this.map.put(id, entry);
	}

	protected abstract ResourceLocation getId(OBJECT object);

	protected abstract void serializeObject(@NotNull IFactoryController controller, @NotNull ENTRY entry, @Nullable OBJECT object, @NotNull CompoundTag tag);

	protected abstract OBJECT deserializeObject(@NotNull IFactoryController controller, @NotNull ENTRY entry, @NotNull CompoundTag tag);

	@NotNull
	public CompoundTag serialize(@NotNull IFactoryController controller, @Nullable OBJECT object)
	{
		var tag = new CompoundTag();
		serialize(controller, object, tag);
		return tag;
	}

	public void serialize(@NotNull IFactoryController controller, @Nullable OBJECT object, @NotNull CompoundTag tag)
	{
		if (object == null)
		{
			return;
		}

		var id = this.getId(object);
		var entry = this.map.get(id);

		if (entry == null)
		{
			throw new IllegalArgumentException("ID " + id + " is not registered");
		}

		tag.putString(TAG_ID, id.toString());
		tag.put(TAG_OBJECT, this.serializeWithoutId(controller, object));
	}

	public CompoundTag serializeWithoutId(@NotNull IFactoryController controller, @Nullable OBJECT object)
	{
		var tag = new CompoundTag();
		this.serializeWithoutId(controller, object, tag);
		return tag;
	}

	public void serializeWithoutId(@NotNull IFactoryController controller, @Nullable OBJECT object, @NotNull CompoundTag tag)
	{
		if (object == null)
		{
			return;
		}

		var id = this.getId(object);
		var entry = this.map.get(id);

		if (entry == null)
		{
			throw new IllegalArgumentException("ID " + id + " is not registered");
		}

		this.serializeObject(controller, entry, object, tag);
	}

	@Nullable
	public OBJECT deserialize(@NotNull IFactoryController controller, @NotNull CompoundTag tag)
	{
		var id = new ResourceLocation(tag.getString(TAG_ID));
		return deserializeWithoutId(controller, tag.getCompound(TAG_OBJECT), id);
	}

	public OBJECT deserializeWithoutId(@NotNull IFactoryController controller, @NotNull CompoundTag tag, ResourceLocation id)
	{
		var entry = this.map.get(id);

		if (entry == null)
		{
			return null;
		}

		return this.deserializeObject(controller, entry, tag);
	}

	public static abstract class Entry
	{
		@NotNull
		private ResourceLocation id;

		public Entry(@NotNull ResourceLocation id)
		{
			this.id = id;
		}

		@NotNull
		public ResourceLocation getId()
		{
			return this.id;
		}

	}

}
