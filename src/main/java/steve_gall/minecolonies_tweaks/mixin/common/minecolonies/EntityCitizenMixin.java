package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecolonies.core.entity.citizen.EntityCitizen;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

@Mixin(value = EntityCitizen.class, remap = false)
public abstract class EntityCitizenMixin
{
	@Shadow(remap = false)
	private int interactionCooldown;

	@Shadow(remap = false)
	abstract InteractionResult directPlayerInteraction(Player player, InteractionHand hand);

	@Redirect(method = "checkAndHandleImportantInteractions", remap = false, at = @At(value = "INVOKE", target = "directPlayerInteraction", remap = false))
	private InteractionResult checkAndHandleImportantInteractions(EntityCitizen self, Player player, InteractionHand hand)
	{
		var result = this.directPlayerInteraction(player, hand);

		if (MineColoniesTweaksConfigServer.INSTANCE.citizens.disableInteractionDelay.get())
		{
			this.interactionCooldown = 0;
		}

		return result;
	}

}
