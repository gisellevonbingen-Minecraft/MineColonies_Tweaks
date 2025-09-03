package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.research.ILocalResearchTree;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingUniversity;
import com.minecolonies.core.network.messages.server.colony.building.university.TryResearchMessage;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;
import steve_gall.minecolonies_tweaks.core.common.research.LocalResearchTreeExtension;

@Mixin(value = TryResearchMessage.class)
public abstract class TryResearchMessageMixin
{
	@WrapOperation(method = "onExecute", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z", remap = true))
	private boolean onExecute_isCreative(ServerPlayer player, Operation<Boolean> operation)
	{
		return operation.call(player) || MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get();
	}

	@WrapOperation(method = "onExecute", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/ILocalResearchTree.attemptBeginResearch", remap = false))
	private void onExecute_attemptBeginResearch(ILocalResearchTree tree, Player player, IColony colony, IGlobalResearch research, Operation<Void> operation, @Local(argsOnly = true) BuildingUniversity building)
	{
		try
		{
			if (tree instanceof LocalResearchTreeExtension extension)
			{
				extension.minecolonies_tweaks$setBuilding(building);
			}

			operation.call(tree, player, colony, research);
		}
		finally
		{
			if (tree instanceof LocalResearchTreeExtension extension)
			{
				extension.minecolonies_tweaks$setBuilding(null);
			}

		}

	}

}
