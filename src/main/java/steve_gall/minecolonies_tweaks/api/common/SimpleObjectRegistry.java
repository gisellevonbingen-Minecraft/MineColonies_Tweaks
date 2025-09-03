package steve_gall.minecolonies_tweaks.api.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public abstract class SimpleObjectRegistry<OBJECT> extends CustomizableObjectRegistry<OBJECT, SimpleObjectRegistry.Entry<OBJECT>>
{
	@SuppressWarnings("unchecked")
	public <TYPED_OBJECT extends OBJECT> void register(@NotNull ResourceLocation id, @NotNull StaticSerializer<TYPED_OBJECT> serializer, @NotNull Deserializer<TYPED_OBJECT> deserializer)
	{
		super.register((Entry<OBJECT>) new Entry<>(id, serializer, deserializer));
	}

	@SuppressWarnings("unchecked")
	public <TYPED_OBJECT extends OBJECT> void register(@NotNull ResourceLocation id, @NotNull InstanceSerializer<TYPED_OBJECT> serializer, @NotNull Deserializer<TYPED_OBJECT> deserializer)
	{
		super.register((Entry<OBJECT>) new Entry<>(id, (provider, controller, tag, object) -> serializer.serializeObject(object, provider, controller, tag), deserializer));
	}

	@Override
	protected void serializeObject(@NotNull HolderLookup.Provider provider, @NotNull IFactoryController controller, @NotNull CompoundTag tag, @NotNull Entry<OBJECT> entry, @Nullable OBJECT object)
	{
		entry.serializer.serializeObject(provider, controller, tag, object);
	}

	@Override
	protected OBJECT deserializeObject(@NotNull HolderLookup.Provider provider, @NotNull IFactoryController controller, @NotNull CompoundTag tag, @NotNull Entry<OBJECT> entry)
	{
		return entry.deserializer.deserializeObject(provider, controller, tag);
	}

	public static class Entry<OBJECT> extends CustomizableObjectRegistry.Entry
	{
		@NotNull
		private StaticSerializer<OBJECT> serializer;
		@NotNull
		private Deserializer<OBJECT> deserializer;

		public Entry(@NotNull ResourceLocation id, @NotNull StaticSerializer<OBJECT> serializer, @NotNull Deserializer<OBJECT> deserializer)
		{
			super(id);
			this.serializer = serializer;
			this.deserializer = deserializer;
		}

	}

	@FunctionalInterface
	public static interface StaticSerializer<OBJECT>
	{
		void serializeObject(@NotNull HolderLookup.Provider provider, @NotNull IFactoryController controller, @NotNull CompoundTag tag, @Nullable OBJECT object);
	}

	@FunctionalInterface
	public static interface InstanceSerializer<OBJECT>
	{
		void serializeObject(@Nullable OBJECT object, @NotNull HolderLookup.Provider provider, @NotNull IFactoryController controller, @NotNull CompoundTag tag);
	}

	@FunctionalInterface
	public static interface Deserializer<OBJECT>
	{
		OBJECT deserializeObject(@NotNull HolderLookup.Provider provider, @NotNull IFactoryController controller, @NotNull CompoundTag tag);
	}

}
