package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.requestable.Stack;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.AbstractBuildingContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import steve_gall.minecolonies_tweaks.core.common.requestsystem.StackExtension;

@Mixin(value = AbstractBuilding.class, remap = false)
public abstract class AbstractBuildingMixin extends AbstractBuildingContainer
{
	public AbstractBuildingMixin(BlockPos pos, IColony colony)
	{
		super(pos, colony);
	}

	@Inject(method = "onRequestedRequestComplete", remap = false, at = @At(value = "HEAD"))
	private void onRequestedRequestComplete(IRequestManager manager, IRequest<?> request, CallbackInfo ci)
	{
		if (request.getRequest() instanceof Stack stack)
		{
			var uuid = ((StackExtension) stack).minecolonies_tweaks$getRequester();

			if (uuid != null)
			{
				var player = this.colony.getWorld().getServer().getPlayerList().getPlayer(uuid);

				if (player != null)
				{
					var itemStack = stack.getStack();
					var itemStackComponent = ComponentUtils.wrapInSquareBrackets(request.getShortDisplayString());
					itemStackComponent.withStyle(itemStack.getRarity().getStyleModifier());
					itemStackComponent.withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(itemStack))));
					player.sendSystemMessage(Component.translatable("minecolonies_tweaks.text.postbox_delivery_completed", itemStackComponent));
				}

			}

		}

	}

}
