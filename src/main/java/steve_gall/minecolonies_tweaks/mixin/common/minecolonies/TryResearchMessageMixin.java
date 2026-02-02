package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.core.network.messages.server.colony.building.university.TryResearchMessage;

import net.minecraft.server.level.ServerPlayer;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = TryResearchMessage.class)
public abstract class TryResearchMessageMixin
{
	@WrapOperation(method = "onExecute", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z", remap = true))
	private boolean onExecute_isCreative(ServerPlayer player, Operation<Boolean> operation)
	{
		return operation.call(player) || MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get();
	}

}
