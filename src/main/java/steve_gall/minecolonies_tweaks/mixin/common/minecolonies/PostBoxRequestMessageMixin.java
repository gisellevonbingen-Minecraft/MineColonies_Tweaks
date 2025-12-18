package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.minecolonies.api.colony.requestsystem.requestable.IRequestable;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.core.colony.buildings.workerbuildings.PostBox;
import com.minecolonies.core.network.messages.server.colony.building.postbox.PostBoxRequestMessage;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.core.common.requestsystem.StackExtension;

@Mixin(value = PostBoxRequestMessage.class, remap = false)
public abstract class PostBoxRequestMessageMixin
{
	@WrapOperation(method = "onExecute", remap = false, at = @At(value = "INVOKE", target = "Lcom/minecolonies/core/colony/buildings/workerbuildings/PostBox;createRequest(Lcom/minecolonies/api/colony/requestsystem/requestable/IRequestable;Z)Lcom/minecolonies/api/colony/requestsystem/token/IToken;", remap = false))
	private IToken<?> createRequest(PostBox building, IRequestable requested, boolean async, Operation<IToken<?>> operation, @Local(argsOnly = true) IPayloadContext ctxIn)
	{
		var token = operation.call(building, requested, async);
		var request = building.getColony().getRequestManager().getRequestForToken(token);

		if (request != null && request.getRequest() instanceof StackExtension extension)
		{
			extension.minecolonies_tweaks$setRequester(ctxIn.player().getUUID());
		}

		return token;
	}

}
