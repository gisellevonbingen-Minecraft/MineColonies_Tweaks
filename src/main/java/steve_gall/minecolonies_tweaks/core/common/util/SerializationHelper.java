package steve_gall.minecolonies_tweaks.core.common.util;

import java.util.function.Function;

import com.minecolonies.api.colony.requestsystem.StandardFactoryController;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class SerializationHelper
{
	public static <OUTPUT> Function<CompoundTag, OUTPUT> deserializerTag()
	{
		return StandardFactoryController.getInstance()::deserialize;
	}

	public static <INPUT> Function<INPUT, CompoundTag> serializerTag()
	{
		return StandardFactoryController.getInstance()::serialize;
	}

	public static <OUTPUT> FriendlyByteBuf.Reader<OUTPUT> deserializer()
	{
		return StandardFactoryController.getInstance()::deserialize;
	}

	public static <INPUT> FriendlyByteBuf.Writer<INPUT> serializer()
	{
		return StandardFactoryController.getInstance()::serialize;
	}

	private SerializationHelper()
	{

	}

}
