package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.requestable.IDeliverable;
import com.minecolonies.api.colony.requestsystem.requestable.Stack;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import steve_gall.minecolonies_tweaks.core.common.requestsystem.StackExtension;

@Mixin(value = Stack.class, remap = false)
public abstract class StackMixin implements StackExtension
{
	@Unique
	private UUID minecolonies_tweaks$requester;

	@Inject(method = "serialize(Lnet/minecraft/core/HolderLookup$Provider;Lcom/minecolonies/api/colony/requestsystem/factory/IFactoryController;Lcom/minecolonies/api/colony/requestsystem/requestable/Stack;)Lnet/minecraft/nbt/CompoundTag;", remap = false, at = @At(value = "TAIL"))
	private static void serialize(HolderLookup.Provider provider, IFactoryController controller, Stack input, CallbackInfoReturnable<CompoundTag> cir)
	{
		UUID requester = ((StackExtension) input).minecolonies_tweaks$getRequester();

		if (requester != null)
		{
			cir.getReturnValue().putUUID("minecolonies_tweaks$requester", requester);

		}

	}

	@Inject(method = "deserialize(Lnet/minecraft/core/HolderLookup$Provider;Lcom/minecolonies/api/colony/requestsystem/factory/IFactoryController;Lnet/minecraft/nbt/CompoundTag;)Lcom/minecolonies/api/colony/requestsystem/requestable/Stack;", remap = false, at = @At(value = "TAIL"))
	private static void deserialize(HolderLookup.Provider provider, IFactoryController controller, CompoundTag compound, CallbackInfoReturnable<Stack> cir)
	{
		if (compound.hasUUID("minecolonies_tweaks$requester"))
		{
			((StackExtension) cir.getReturnValue()).minecolonies_tweaks$setRequester(compound.getUUID("minecolonies_tweaks$requester"));
		}

	}

	@Inject(method = "serialize(Lcom/minecolonies/api/colony/requestsystem/factory/IFactoryController;Lnet/minecraft/network/RegistryFriendlyByteBuf;Lcom/minecolonies/api/colony/requestsystem/requestable/Stack;)V", remap = false, at = @At(value = "TAIL"))
	private static void serialize(IFactoryController controller, RegistryFriendlyByteBuf buffer, Stack input, CallbackInfo ci)
	{
		buffer.writeNullable(((StackExtension) input).minecolonies_tweaks$getRequester(), RegistryFriendlyByteBuf::writeUUID);
	}

	@Inject(method = "deserialize(Lcom/minecolonies/api/colony/requestsystem/factory/IFactoryController;Lnet/minecraft/network/RegistryFriendlyByteBuf;)Lcom/minecolonies/api/colony/requestsystem/requestable/Stack;", remap = false, at = @At(value = "TAIL"))
	private static void deserialize(IFactoryController controller, RegistryFriendlyByteBuf buffer, CallbackInfoReturnable<Stack> cir)
	{
		((StackExtension) cir.getReturnValue()).minecolonies_tweaks$setRequester(buffer.readNullable(RegistryFriendlyByteBuf::readUUID));
	}

	@Inject(method = "copyWithCount", remap = false, at = @At(value = "RETURN"))
	private void copyWithCount(int newCount, CallbackInfoReturnable<IDeliverable> cir)
	{
		((StackExtension) cir.getReturnValue()).minecolonies_tweaks$setRequester(this.minecolonies_tweaks$requester);
	}

	@Override
	public void minecolonies_tweaks$setRequester(UUID uuid)
	{
		this.minecolonies_tweaks$requester = uuid;
	}

	@Override
	public UUID minecolonies_tweaks$getRequester()
	{
		return this.minecolonies_tweaks$requester;
	}

}
