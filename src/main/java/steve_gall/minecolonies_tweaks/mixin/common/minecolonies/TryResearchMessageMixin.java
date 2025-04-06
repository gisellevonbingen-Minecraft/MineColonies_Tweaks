package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecolonies.core.network.messages.server.colony.building.university.TryResearchMessage;

import net.minecraft.world.entity.player.Player;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = TryResearchMessage.class)
public abstract class TryResearchMessageMixin
{
	@Redirect(method = "onExecute", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isCreative()Z", remap = true))
	private boolean onExecute_isCreative(Player player)
	{
		return player.isCreative() || MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get();
	}

}
