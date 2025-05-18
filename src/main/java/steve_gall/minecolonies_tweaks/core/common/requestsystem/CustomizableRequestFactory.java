package steve_gall.minecolonies_tweaks.core.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.request.IRequestFactory;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IFriendlyByteBufToObjectReader;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.INBTToObjectConverter;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IObjectToNBTConverter;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IObjectToPackBufferWriter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.ICustomizableRequestable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.RequestFactoryHelper;

public abstract class CustomizableRequestFactory<T extends ICustomizableRequestable, R extends CustomizableRequest<T>> implements IRequestFactory<T, R>
{
	public abstract NewFactory<T, R> getNewFactory();

	public abstract INBTToObjectConverter<T> getNBTDeserializer();

	public abstract IObjectToNBTConverter<T> getNBTSerializer();

	public abstract IFriendlyByteBufToObjectReader<T> getByteBufDeserializer();

	public abstract IObjectToPackBufferWriter<T> getByteBufSerializer();

	@Override
	public R getNewInstance(@NotNull T input, @NotNull IRequester location, @NotNull IToken<?> token, @NotNull RequestState initialState)
	{
		return this.getNewFactory().getNewInstance(location, token, initialState, input);
	}

	@NotNull
	@Override
	public R deserialize(@NotNull IFactoryController controller, @NotNull CompoundTag nbt) throws Throwable
	{
		return StandardRequestFactories.deserializeFromNBT(controller, nbt, this.getNBTDeserializer(), RequestFactoryHelper.getObjectConstructor(controller, this.getFactoryOutputType()));
	}

	@Override
	@NotNull
	public CompoundTag serialize(@NotNull IFactoryController controller, @NotNull R output)
	{
		return StandardRequestFactories.serializeToNBT(controller, output, this.getNBTSerializer());
	}

	@Override
	public @NotNull R deserialize(@NotNull IFactoryController controller, @NotNull FriendlyByteBuf buffer) throws Throwable
	{
		return StandardRequestFactories.deserializeFromFriendlyByteBuf(controller, buffer, this.getByteBufDeserializer(), RequestFactoryHelper.getObjectConstructor(controller, this.getFactoryOutputType()));
	}

	@Override
	public void serialize(@NotNull IFactoryController controller, @NotNull R output, FriendlyByteBuf packetBuffer)
	{
		StandardRequestFactories.serializeToFriendlyByteBuf(controller, output, packetBuffer, this.getByteBufSerializer());
	}

	@FunctionalInterface
	public interface NewFactory<T, R>
	{
		R getNewInstance(@NotNull IRequester requester, @NotNull IToken<?> token, @NotNull RequestState state, @NotNull T requested);
	}

}
