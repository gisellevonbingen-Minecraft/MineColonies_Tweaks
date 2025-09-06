package steve_gall.minecolonies_tweaks.core.common.item;

import java.util.function.Function;

import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ItemSerializationHelper
{
	public static void serialize(FriendlyByteBuf buf, ItemStack stack)
	{
		Utils.serializeCodecMess((RegistryFriendlyByteBuf) buf, stack);
	}

	public static ItemStack deserialize(FriendlyByteBuf buf)
	{
		return ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);
	}

	public static Function<ItemStack, CompoundTag> serializerTag(HolderLookup.Provider provider)
	{
		return stack -> serializeTag(provider, stack);
	}

	public static Function<CompoundTag, ItemStack> deserializerTag(HolderLookup.Provider provider)
	{
		return tag -> deserializeTag(provider, tag);
	}

	public static CompoundTag serializeTag(HolderLookup.Provider provider, ItemStack stack)
	{
		return (CompoundTag) stack.saveOptional(provider);
	}

	public static ItemStack deserializeTag(HolderLookup.Provider provider, CompoundTag tag)
	{
		return ItemStackUtils.deserializeFromNBT(tag, provider);
	}

	private ItemSerializationHelper()
	{

	}

}
