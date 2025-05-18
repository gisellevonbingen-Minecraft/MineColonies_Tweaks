package steve_gall.minecolonies_tweaks.api.common.requestsystem.resolvers;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.resolver.IRequestResolverFactory;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.constant.TypeConstants;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class CustomizableRequestResolverFactory<RESOLVER extends ICustomizableRequestResolver<?>> implements IRequestResolverFactory<RESOLVER>
{
	private final TypeToken<RESOLVER> outputType;
	private final short serializationId;
	private final Serializer<RESOLVER> serializer;
	private final Deserializer<RESOLVER> deserializer;

	public CustomizableRequestResolverFactory(@NotNull Class<RESOLVER> clazz, short serializationId, @NotNull Serializer<RESOLVER> serializer, @NotNull Deserializer<RESOLVER> deserializer)
	{
		this.outputType = TypeToken.of(clazz);
		this.serializationId = serializationId;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public @NotNull TypeToken<RESOLVER> getFactoryOutputType()
	{
		return this.outputType;
	}

	@Override
	public @NotNull TypeToken<? extends ILocation> getFactoryInputType()
	{
		return TypeConstants.ILOCATION;
	}

	@Override
	public short getSerializationId()
	{
		return this.serializationId;
	}

	@Override
	public @NotNull RESOLVER getNewInstance(@NotNull IFactoryController factoryController, @NotNull ILocation input, @NotNull Object... context) throws IllegalArgumentException
	{
		throw new NotImplementedException();
	}

	@Override
	public @NotNull CompoundTag serialize(@NotNull IFactoryController controller, @NotNull RESOLVER output)
	{
		var tag = new CompoundTag();
		tag.put("location", controller.serialize(output.getLocation()));
		tag.put("token", controller.serialize(output.getId()));

		var impl = new CompoundTag();
		this.serializer.serialize(output, impl);
		tag.put("impl", impl);
		return tag;
	}

	@Override
	public @NotNull RESOLVER deserialize(@NotNull IFactoryController controller, @NotNull CompoundTag tag) throws Throwable
	{
		ILocation location = controller.deserialize(tag.getCompound("location"));
		IToken<?> token = controller.deserialize(tag.getCompound("token"));
		return this.deserializer.deserialze(location, token, tag.getCompound("impl"));
	}

	@Override
	public void serialize(@NotNull IFactoryController controller, @NotNull RESOLVER output, FriendlyByteBuf packetBuffer)
	{
		packetBuffer.writeNbt(this.serialize(controller, output));
	}

	@Override
	public @NotNull RESOLVER deserialize(@NotNull IFactoryController controller, @NotNull FriendlyByteBuf buffer) throws Throwable
	{
		return this.deserialize(controller, buffer.readNbt());
	}

	@FunctionalInterface
	public static interface Serializer<RESOLVER extends ICustomizableRequestResolver<?>>
	{
		void serialize(@NotNull RESOLVER resolver, @NotNull CompoundTag compound);
	}

	@FunctionalInterface
	public static interface Deserializer<RESOLVER extends ICustomizableRequestResolver<?>>
	{
		RESOLVER deserialze(@NotNull ILocation location, @NotNull IToken<?> token, @NotNull CompoundTag compound);
	}

}
