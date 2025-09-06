package steve_gall.minecolonies_tweaks.core.common.util;

import java.util.function.Function;

import com.minecolonies.api.colony.requestsystem.StandardFactoryController;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;

public class SerializationHelper
{
	public static <OUTPUT> Function<CompoundTag, OUTPUT> deserializerTag(HolderLookup.Provider provider)
	{
		return tag -> StandardFactoryController.getInstance().deserializeTag(provider, tag);
	}

	public static <INPUT> Function<INPUT, CompoundTag> serializerTag(HolderLookup.Provider provider)
	{
		return input -> StandardFactoryController.getInstance().serializeTag(provider, input);
	}

	public static <INPUT, OUTPUT> Function<INPUT, OUTPUT> apply(HolderLookup.Provider provider, ProviderTransformer<INPUT, OUTPUT> apply)
	{
		return input -> apply.apply(provider, input);
	}

	public static <OUTPUT> OUTPUT deserialize(FriendlyByteBuf buf)
	{
		return StandardFactoryController.getInstance().deserialize((RegistryFriendlyByteBuf) buf);
	}

	public static <INPUT> void serialize(FriendlyByteBuf buf, INPUT input)
	{
		StandardFactoryController.getInstance().serialize((RegistryFriendlyByteBuf) buf, input);
	}

	public static <INPUT> StreamDecoder<FriendlyByteBuf, INPUT> reader(WrappedStreamDecoder<INPUT> reader)
	{
		return buf -> reader.apply((RegistryFriendlyByteBuf) buf);
	}

	public static <OUTPUT> StreamEncoder<FriendlyByteBuf, OUTPUT> writer(WrappedStreamEncoder<OUTPUT> writer)
	{
		return (buf, output) -> writer.accept((RegistryFriendlyByteBuf) buf, output);
	}

	private SerializationHelper()
	{

	}

	@FunctionalInterface
	public static interface ProviderTransformer<INPUT, OUTPUT>
	{
		OUTPUT apply(HolderLookup.Provider provider, INPUT input);
	}

	@FunctionalInterface
	public static interface WrappedStreamDecoder<OUTPUT>
	{
		OUTPUT apply(RegistryFriendlyByteBuf buf);
	}

	@FunctionalInterface
	public static interface WrappedStreamEncoder<INPUT>
	{
		void accept(RegistryFriendlyByteBuf buf, INPUT input);
	}

}
