package steve_gall.minecolonies_tweaks.api.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public abstract class CustomizableObjectRegistry<OBJECT>
{
	public static final String TAG_ID = "ID";
	public static final String TAG_OBJECT = "Object";

	public static final ResourceLocation EMPTY_ID = MineColoniesTweaks.rl("empty");
	public static final String EMPTY_ID_STRING = EMPTY_ID.toString();

	private final Map<ResourceLocation, Entry<?>> map = new HashMap<>();

	public CustomizableObjectRegistry()
	{

	}

	public <TYPED_OBJECT extends OBJECT> void register(@NotNull ResourceLocation id, @NotNull BiConsumer<TYPED_OBJECT, CompoundTag> serializer, @NotNull Function<CompoundTag, TYPED_OBJECT> desrializer)
	{
		if (this.map.containsKey(id))
		{
			throw new IllegalArgumentException("ID " + id + " is already registered");
		}

		this.map.put(id, new Entry<>(id, serializer, desrializer));
	}

	protected abstract ResourceLocation getId(OBJECT object);

	@NotNull
	public CompoundTag serialize(@Nullable OBJECT object)
	{
		var tag = new CompoundTag();
		serialize(object, tag);
		return tag;
	}

	public void serialize(@Nullable OBJECT object, @NotNull CompoundTag tag)
	{
		if (object == null)
		{
			return;
		}

		var id = this.getId(object);
		var supplier = this.map.get(id);

		if (supplier == null)
		{
			throw new IllegalArgumentException("ID " + id + " is not registered");
		}

		tag.putString(TAG_ID, id.toString());
		tag.put(TAG_OBJECT, serializeWithoutId(object));
	}

	public CompoundTag serializeWithoutId(@Nullable OBJECT object)
	{
		var tag = new CompoundTag();
		serializeWithoutId(object, tag);
		return tag;
	}

	public void serializeWithoutId(@Nullable OBJECT object, @NotNull CompoundTag tag)
	{
		if (object == null)
		{
			return;
		}

		var id = this.getId(object);
		var supplier = this.map.get(id);

		if (supplier == null)
		{
			throw new IllegalArgumentException("ID " + id + " is not registered");
		}

		@SuppressWarnings("unchecked")
		var serializer = (BiConsumer<OBJECT, CompoundTag>) supplier.serializer;
		serializer.accept(object, tag);
	}

	@Nullable
	public OBJECT deserialize(@NotNull CompoundTag tag)
	{
		var id = new ResourceLocation(tag.getString(TAG_ID));
		return deserializeWithoutId(tag.getCompound(TAG_OBJECT), id);
	}

	@SuppressWarnings("unchecked")
	public OBJECT deserializeWithoutId(@NotNull CompoundTag tag, ResourceLocation id)
	{
		var supplier = this.map.get(id);

		if (supplier == null)
		{
			return null;
		}

		return (OBJECT) supplier.deserializer.apply(tag);
	}

	private static class Entry<OBJECT>
	{
		@NotNull
		private ResourceLocation id;
		@NotNull
		private BiConsumer<OBJECT, CompoundTag> serializer;
		@NotNull
		private Function<CompoundTag, OBJECT> deserializer;

		public Entry(@NotNull ResourceLocation id, @NotNull BiConsumer<OBJECT, CompoundTag> serializer, @NotNull Function<CompoundTag, OBJECT> deserializer)
		{
			this.id = id;
			this.serializer = serializer;
			this.deserializer = deserializer;
		}

	}

}
