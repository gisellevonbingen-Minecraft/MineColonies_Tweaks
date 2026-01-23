package steve_gall.minecolonies_tweaks.core.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import com.google.common.reflect.TypeToken;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IFriendlyByteBufToObjectReader;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.INBTToObjectConverter;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IObjectToNBTConverter;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequestFactories.IObjectToPackBufferWriter;

import steve_gall.minecolonies_tweaks.api.common.SerializationIds;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;

public class CustomizableRequestableRequestFactory extends CustomizableRequestFactory<CustomizableRequestable, CustomizableRequestableRequest>
{
	public static final TypeToken<CustomizableRequestable> INPUT_TYPE = TypeToken.of(CustomizableRequestable.class);
	public static final TypeToken<CustomizableRequestableRequest> OUPYT_TYPE = TypeToken.of(CustomizableRequestableRequest.class);

	@Override
	@NotNull
	public TypeToken<? extends CustomizableRequestable> getFactoryInputType()
	{
		return INPUT_TYPE;
	}

	@Override
	@NotNull
	public TypeToken<? extends CustomizableRequestableRequest> getFactoryOutputType()
	{
		return OUPYT_TYPE;
	}

	@Override
	public short getSerializationId()
	{
		return SerializationIds.CUSTOMIZABLE_REQUESTABLE_REQUEST;
	}

	@Override
	public NewFactory<CustomizableRequestable, CustomizableRequestableRequest> getNewFactory()
	{
		return CustomizableRequestableRequest::new;
	}

	@Override
	public INBTToObjectConverter<CustomizableRequestable> getNBTDeserializer()
	{
		return CustomizableRequestable::deserialize;
	}

	@Override
	public IObjectToNBTConverter<CustomizableRequestable> getNBTSerializer()
	{
		return CustomizableRequestable::serialize;
	}

	@Override
	public IFriendlyByteBufToObjectReader<CustomizableRequestable> getByteBufDeserializer()
	{
		return CustomizableRequestable::deserialize;
	}

	@Override
	public IObjectToPackBufferWriter<CustomizableRequestable> getByteBufSerializer()
	{
		return CustomizableRequestable::serialize;
	}

}
