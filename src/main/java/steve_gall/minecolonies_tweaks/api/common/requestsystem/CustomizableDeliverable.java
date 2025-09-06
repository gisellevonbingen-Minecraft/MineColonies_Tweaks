package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.requestable.IDeliverable;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.ReflectionUtils;
import com.minecolonies.api.util.constant.TypeConstants;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.common.CustomizableObjectRegistry;
import steve_gall.minecolonies_tweaks.core.common.item.ItemSerializationHelper;

public class CustomizableDeliverable implements ICustomizableRequestable, IDeliverable
{
	public static final TypeToken<CustomizableDeliverable> TYPE_TOKEN = TypeToken.of(CustomizableDeliverable.class);
	public static final Set<TypeToken<?>> TYPE_TOKENS = ReflectionUtils.getSuperClasses(TYPE_TOKEN).stream().filter(type -> !type.equals(TypeConstants.OBJECT)).collect(Collectors.toSet());

	public static final String TAG_ID = "ID";
	public static final String TAG_OBJECT = "Object";
	public static final String TAG_RESULT = "Result";

	@NotNull
	private final ResourceLocation id;
	@Nullable
	private final IDeliverableObject object;

	@NotNull
	private ItemStack result;

	public CustomizableDeliverable(@Nullable IDeliverableObject object)
	{
		this.id = object != null ? object.getId() : CustomizableObjectRegistry.EMPTY_ID;
		this.object = object;
		this.result = ItemStack.EMPTY;
	}

	public CustomizableDeliverable(@NotNull ResourceLocation id, @Nullable IDeliverableObject object, @NotNull ItemStack result)
	{
		this.id = id;
		this.object = object;
		this.result = result;
	}

	@NotNull
	public static CompoundTag serialize(@NotNull HolderLookup.Provider provider, @NotNull IFactoryController controller, @NotNull CustomizableDeliverable input)
	{
		var compound = new CompoundTag();
		compound.putString(TAG_ID, input.getId().toString());
		compound.put(TAG_OBJECT, DeliverableObjectRegistry.INSTANCE.serializeWithoutId(provider, controller, input.getObject()));
		compound.put(TAG_RESULT, ItemSerializationHelper.serializeTag(provider, input.getResult()));
		return compound;
	}

	@NotNull
	public static CustomizableDeliverable deserialize(@NotNull HolderLookup.Provider provider, @NotNull IFactoryController controller, @NotNull CompoundTag compound)
	{
		var id = ResourceLocation.parse(compound.getString(TAG_ID));
		var object = DeliverableObjectRegistry.INSTANCE.deserializeWithoutId(provider, controller, compound.getCompound(TAG_OBJECT), id);
		var result = ItemSerializationHelper.deserializeTag(provider, compound.getCompound(TAG_RESULT));
		return new CustomizableDeliverable(id, object, result);
	}

	public static void serialize(@NotNull IFactoryController controller, @NotNull RegistryFriendlyByteBuf buffer, @NotNull CustomizableDeliverable input)
	{
		buffer.writeResourceLocation(input.getId());
		buffer.writeNbt(DeliverableObjectRegistry.INSTANCE.serializeWithoutId(buffer.registryAccess(), controller, input.getObject()));
		ItemSerializationHelper.serialize(buffer, input.getResult());
	}

	@NotNull
	public static CustomizableDeliverable deserialize(@NotNull IFactoryController controller, @NotNull RegistryFriendlyByteBuf buffer)
	{
		var id = buffer.readResourceLocation();
		var object = DeliverableObjectRegistry.INSTANCE.deserializeWithoutId(buffer.registryAccess(), controller, buffer.readNbt(), id);
		var result = ItemSerializationHelper.deserialize(buffer);
		return new CustomizableDeliverable(id, object, result);
	}

	@Override
	public boolean matches(@NotNull ItemStack stack)
	{
		var object = this.getObject();
		return object != null && object.matches(stack);
	}

	@Override
	@NotNull
	public ResourceLocation getId()
	{
		return this.id;
	}

	@Override
	@Nullable
	public IDeliverableObject getObject()
	{
		return this.object;
	}

	@Override
	public void setResult(@NotNull ItemStack result)
	{
		this.result = result;
	}

	@Override
	@NotNull
	public CustomizableDeliverable copyWithCount(int newCount)
	{
		var object = this.getObject();
		return new CustomizableDeliverable(this.id, object != null ? object.copyWithCount(newCount) : null, this.result);
	}

	@Override
	public int getCount()
	{
		var object = this.getObject();
		return object != null ? object.getCount() : 0;
	}

	@Override
	public int getMinimumCount()
	{
		var object = this.getObject();
		return object != null ? object.getMinimumCount() : 0;
	}

	@Override
	@NotNull
	public ItemStack getResult()
	{
		return this.result;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (o instanceof CustomizableDeliverable other)
		{
			return Objects.equals(this.getObject(), other.getObject()) && ItemStackUtils.compareItemStacksIgnoreStackSize(this.getResult(), other.getResult());
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
		return (object != null ? object.hashCode() : 0) * 31 + this.getResult().hashCode();
	}

	@Override
	public Set<TypeToken<?>> getSuperClasses()
	{
		return TYPE_TOKENS;
	}

}
