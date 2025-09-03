package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.requestable.IRequestable;
import com.minecolonies.api.util.ReflectionUtils;
import com.minecolonies.api.util.constant.TypeConstants;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.api.common.CustomizableObjectRegistry;

public class CustomizableRequestable implements ICustomizableRequestable, IRequestable
{
	public static final TypeToken<CustomizableRequestable> TYPE_TOKEN = TypeToken.of(CustomizableRequestable.class);
	public static final Set<TypeToken<?>> TYPE_TOKENS = ReflectionUtils.getSuperClasses(TYPE_TOKEN).stream().filter(type -> !type.equals(TypeConstants.OBJECT)).collect(Collectors.toSet());

	public static final String TAG_ID = "ID";
	public static final String TAG_OBJECT = "Object";

	@NotNull
	private final ResourceLocation id;
	@Nullable
	private final IRequestableObject object;

	public CustomizableRequestable(@Nullable IRequestableObject object)
	{
		this.id = object != null ? object.getId() : CustomizableObjectRegistry.EMPTY_ID;
		this.object = object;
	}

	public CustomizableRequestable(@NotNull ResourceLocation id, @Nullable IRequestableObject object)
	{
		this.id = id;
		this.object = object;
	}

	@NotNull
	public static CompoundTag serialize(@NotNull HolderLookup.Provider provider, @NotNull IFactoryController controller, @NotNull CustomizableRequestable input)
	{
		var compound = new CompoundTag();
		compound.putString(TAG_ID, input.getId().toString());
		compound.put(TAG_OBJECT, RequestableObjectRegistry.INSTANCE.serializeWithoutId(provider, controller, input.getObject()));
		return compound;
	}

	@NotNull
	public static CustomizableRequestable deserialize(@NotNull HolderLookup.Provider provider, @NotNull IFactoryController controller, @NotNull CompoundTag compound)
	{
		var id = ResourceLocation.parse(compound.getString(TAG_ID));
		var object = RequestableObjectRegistry.INSTANCE.deserializeWithoutId(provider, controller, compound.getCompound(TAG_OBJECT), id);
		return new CustomizableRequestable(id, object);
	}

	public static void serialize(@NotNull IFactoryController controller, @NotNull RegistryFriendlyByteBuf buffer, @NotNull CustomizableRequestable input)
	{
		buffer.writeResourceLocation(input.getId());
		buffer.writeNbt(RequestableObjectRegistry.INSTANCE.serializeWithoutId(buffer.registryAccess(), controller, input.getObject()));
	}

	@NotNull
	public static CustomizableRequestable deserialize(@NotNull IFactoryController controller, @NotNull RegistryFriendlyByteBuf buffer)
	{
		var id = buffer.readResourceLocation();
		var object = RequestableObjectRegistry.INSTANCE.deserializeWithoutId(buffer.registryAccess(), controller, buffer.readNbt(), id);
		return new CustomizableRequestable(id, object);
	}

	@Override
	@NotNull
	public ResourceLocation getId()
	{
		return this.id;
	}

	@Override
	@Nullable
	public IRequestableObject getObject()
	{
		return this.object;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (o instanceof CustomizableRequestable other)
		{
			return Objects.equals(this.getObject(), other.getObject());
		}
		else
		{
			return false;
		}

	}

	@Override
	public int hashCode()
	{
		var object = this.getObject();
		return object != null ? object.hashCode() : 0;
	}

	@Override
	public Set<TypeToken<?>> getSuperClasses()
	{
		return TYPE_TOKENS;
	}

}
