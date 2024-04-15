package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class RequestableObjectRegistry
{
	private static final Map<ResourceLocation, Entry<?>> MAP = new HashMap<>();

	public static final ResourceLocation EMPTY_ID = MineColoniesTweaks.rl("empty");
	public static final String EMPTY_ID_STRING = EMPTY_ID.toString();

	public static <OBJECT extends IRequestableObject> void register(@NotNull ResourceLocation id, @NotNull BiConsumer<CompoundTag, OBJECT> serializer, @NotNull Function<CompoundTag, OBJECT> desrializer)
	{
		if (MAP.containsKey(id))
		{
			throw new IllegalArgumentException("ID " + id + " is already registered");
		}

		MAP.put(id, new Entry<>(id, serializer, desrializer));
	}

	@NotNull
	public static <OBJECT extends IRequestableObject> CompoundTag serialize(@Nullable OBJECT object)
	{
		var tag = new CompoundTag();
		serialize(object, tag);
		return tag;
	}

	public static <OBJECT extends IRequestableObject> void serialize(@Nullable OBJECT object, @NotNull CompoundTag tag)
	{
		if (object == null)
		{
			return;
		}

		var id = object.getId();
		var supplier = MAP.get(id);

		if (supplier == null)
		{
			throw new IllegalArgumentException("ID " + id + " is not registered");
		}

		@SuppressWarnings("unchecked")
		var serializer = (BiConsumer<CompoundTag, OBJECT>) supplier.serializer;
		serializer.accept(tag, object);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <OBJECT extends IRequestableObject> OBJECT deserialize(@NotNull ResourceLocation id, @NotNull CompoundTag tag)
	{
		var supplier = MAP.get(id);

		if (supplier == null)
		{
			return null;
		}

		return (OBJECT) supplier.desrializer.apply(tag);
	}

	private RequestableObjectRegistry()
	{

	}

	private static class Entry<REQUEST extends IRequestableObject>
	{
		@NotNull
		private ResourceLocation id;
		@NotNull
		private BiConsumer<CompoundTag, REQUEST> serializer;
		@NotNull
		private Function<CompoundTag, REQUEST> desrializer;

		public Entry(@NotNull ResourceLocation id, @NotNull BiConsumer<CompoundTag, REQUEST> serializer, @NotNull Function<CompoundTag, REQUEST> desrializer)
		{
			this.id = id;
			this.serializer = serializer;
			this.desrializer = desrializer;
		}

	}

}
