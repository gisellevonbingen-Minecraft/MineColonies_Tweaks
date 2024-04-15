package steve_gall.minecolonies_tweaks.apiimpl.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.request.IRequestFactory;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableDeliverable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.RequestFactoryHelper;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.SerializationIds;

public class CustomizableDeliverableRequestFactory implements IRequestFactory<CustomizableDeliverable, CustomizableDeliverableRequest>
{
	@Override
	public CustomizableDeliverableRequest getNewInstance(@NotNull CustomizableDeliverable input, @NotNull IRequester location, @NotNull IToken<?> token, @NotNull RequestState initialState)
	{
		return new CustomizableDeliverableRequest(location, token, initialState, input);
	}

	@Override
	@NotNull
	public TypeToken<? extends CustomizableDeliverableRequest> getFactoryOutputType()
	{
		return TypeToken.of(CustomizableDeliverableRequest.class);
	}

	@Override
	@NotNull
	public TypeToken<? extends CustomizableDeliverable> getFactoryInputType()
	{
		return TypeToken.of(CustomizableDeliverable.class);
	}

	@Override
	@NotNull
	public CompoundTag serialize(@NotNull IFactoryController controller, @NotNull CustomizableDeliverableRequest output)
	{
		return StandardRequestFactories.serializeToNBT(controller, output, CustomizableDeliverable::serialize);
	}

	@NotNull
	@Override
	public CustomizableDeliverableRequest deserialize(@NotNull IFactoryController controller, @NotNull CompoundTag nbt) throws Throwable
	{
		return StandardRequestFactories.deserializeFromNBT(controller, nbt, CustomizableDeliverable::deserialize, RequestFactoryHelper.getObjectConstructor(controller, this.getFactoryOutputType()));
	}

	@Override
	public void serialize(@NotNull IFactoryController controller, @NotNull CustomizableDeliverableRequest output, FriendlyByteBuf packetBuffer)
	{
		StandardRequestFactories.serializeToFriendlyByteBuf(controller, output, packetBuffer, CustomizableDeliverable::serialize);
	}

	@Override
	public @NotNull CustomizableDeliverableRequest deserialize(@NotNull IFactoryController controller, @NotNull FriendlyByteBuf buffer) throws Throwable
	{
		return StandardRequestFactories.deserializeFromFriendlyByteBuf(controller, buffer, CustomizableDeliverable::deserialize, RequestFactoryHelper.getObjectConstructor(controller, this.getFactoryOutputType()));
	}

	@Override
	public short getSerializationId()
	{
		return SerializationIds.CUSTOMIZABLE_DELIVERABLE_REQUEST_ID;
	}

}
