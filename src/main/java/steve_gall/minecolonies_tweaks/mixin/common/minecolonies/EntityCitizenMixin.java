package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.core.entity.citizen.EntityCitizen;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = EntityCitizen.class, remap = false)
public abstract class EntityCitizenMixin
{
	@Shadow(remap = false)
	private int interactionCooldown;

	@Inject(method = "directPlayerInteraction", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void directPlayerInteraction(final Player player, final InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
	{
		if (MCTweaksConfigServer.INSTANCE.citizens.disableInteractionDelay.get())
		{
			this.interactionCooldown = 0;
		}

	}

}
