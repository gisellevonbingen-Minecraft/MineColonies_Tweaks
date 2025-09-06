package steve_gall.minecolonies_tweaks.core.common.fluid;

import java.util.function.Function;

import com.minecolonies.api.util.Utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidSerializationHelper
{
	public static void serialize(FriendlyByteBuf buf, FluidStack stack)
	{
		Utils.serializeCodecMess(FluidStack.STREAM_CODEC, (RegistryFriendlyByteBuf) buf, stack);
	}

	public static FluidStack deserialize(FriendlyByteBuf buf)
	{
		return Utils.deserializeCodecMess(FluidStack.STREAM_CODEC, (RegistryFriendlyByteBuf) buf);
	}

	public static Function<FluidStack, CompoundTag> serializerTag(HolderLookup.Provider provider)
	{
		return stack -> serializeTag(provider, stack);
	}

	public static Function<CompoundTag, FluidStack> deserializerTag(HolderLookup.Provider provider)
	{
		return tag -> deserializeTag(provider, tag);
	}

	public static CompoundTag serializeTag(HolderLookup.Provider provider, FluidStack stack)
	{
		return (CompoundTag) stack.saveOptional(provider);
	}

	public static FluidStack deserializeTag(HolderLookup.Provider provider, CompoundTag tag)
	{
		return FluidStack.parseOptional(provider, tag);
	}

	private FluidSerializationHelper()
	{

	}

}
