package steve_gall.minecolonies_tweaks.api.common;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public abstract class SimpleObjectRegistry<OBJECT> extends CustomizableObjectRegistry<OBJECT, SimpleObjectRegistry.Entry<OBJECT>>
{
	@SuppressWarnings("unchecked")
	public <TYPED_OBJECT extends OBJECT> void register(@NotNull ResourceLocation id, @NotNull BiConsumer<TYPED_OBJECT, CompoundTag> serializer, @NotNull Function<CompoundTag, TYPED_OBJECT> deserializer)
	{
		super.register((Entry<OBJECT>) new Entry<>(id, serializer, deserializer));
	}

	@Override
	protected void serializeObject(@NotNull IFactoryController controller, @NotNull Entry<OBJECT> entry, @Nullable OBJECT object, @NotNull CompoundTag tag)
	{
		entry.serializer.accept(object, tag);
	}

	@Override
	protected OBJECT deserializeObject(@NotNull IFactoryController controller, @NotNull Entry<OBJECT> entry, @NotNull CompoundTag tag)
	{
		return entry.deserializer.apply(tag);
	}

	public static class Entry<OBJECT> extends CustomizableObjectRegistry.Entry
	{
		@NotNull
		private BiConsumer<OBJECT, CompoundTag> serializer;
		@NotNull
		private Function<CompoundTag, OBJECT> deserializer;

		public Entry(@NotNull ResourceLocation id, @NotNull BiConsumer<OBJECT, CompoundTag> serializer, @NotNull Function<CompoundTag, OBJECT> deserializer)
		{
			super(id);
			this.serializer = serializer;
			this.deserializer = deserializer;
		}

	}

}
