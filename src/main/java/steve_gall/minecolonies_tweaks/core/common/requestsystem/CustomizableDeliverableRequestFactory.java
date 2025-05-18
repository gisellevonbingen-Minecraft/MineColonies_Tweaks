package steve_gall.minecolonies_tweaks.core.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import com.google.common.reflect.TypeToken;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IFriendlyByteBufToObjectReader;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.INBTToObjectConverter;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IObjectToNBTConverter;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IObjectToPackBufferWriter;

import steve_gall.minecolonies_tweaks.api.common.SerializationIds;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableDeliverable;

public class CustomizableDeliverableRequestFactory extends CustomizableRequestFactory<CustomizableDeliverable, CustomizableDeliverableRequest>
{
	public static final TypeToken<CustomizableDeliverable> INPUT_TYPE = TypeToken.of(CustomizableDeliverable.class);
	public static final TypeToken<CustomizableDeliverableRequest> OUPYT_TYPE = TypeToken.of(CustomizableDeliverableRequest.class);

	@Override
	@NotNull
	public TypeToken<? extends CustomizableDeliverable> getFactoryInputType()
	{
		return INPUT_TYPE;
	}

	@Override
	@NotNull
	public TypeToken<? extends CustomizableDeliverableRequest> getFactoryOutputType()
	{
		return OUPYT_TYPE;
	}

	@Override
	public short getSerializationId()
	{
		return SerializationIds.CUSTOMIZABLE_DELIVERABLE_REQUEST_ID;
	}

	@Override
	public NewFactory<CustomizableDeliverable, CustomizableDeliverableRequest> getNewFactory()
	{
		return CustomizableDeliverableRequest::new;
	}

	@Override
	public INBTToObjectConverter<CustomizableDeliverable> getNBTDeserializer()
	{
		return CustomizableDeliverable::deserialize;
	}

	@Override
	public IObjectToNBTConverter<CustomizableDeliverable> getNBTSerializer()
	{
		return CustomizableDeliverable::serialize;
	}

	@Override
	public IFriendlyByteBufToObjectReader<CustomizableDeliverable> getByteBufDeserializer()
	{
		return CustomizableDeliverable::deserialize;
	}

	@Override
	public IObjectToPackBufferWriter<CustomizableDeliverable> getByteBufSerializer()
	{
		return CustomizableDeliverable::serialize;
	}

}
