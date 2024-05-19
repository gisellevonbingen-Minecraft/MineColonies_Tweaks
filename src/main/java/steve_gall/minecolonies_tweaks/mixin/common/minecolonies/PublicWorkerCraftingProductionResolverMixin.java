package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.requestable.crafting.PublicCrafting;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.CraftingWorkerBuildingModule;
import com.minecolonies.core.colony.jobs.AbstractJobCrafter;
import com.minecolonies.core.colony.requestsystem.resolvers.PublicWorkerCraftingProductionResolver;
import com.minecolonies.core.colony.requestsystem.resolvers.core.AbstractCraftingProductionResolver;

import net.minecraft.world.item.ItemStack;

@Mixin(value = PublicWorkerCraftingProductionResolver.class, remap = false)
public abstract class PublicWorkerCraftingProductionResolverMixin extends AbstractCraftingProductionResolver<PublicCrafting>
{
	public PublicWorkerCraftingProductionResolverMixin(@NotNull ILocation location, @NotNull IToken<?> token, @NotNull JobEntry jobEntry, Class<PublicCrafting> cClass)
	{
		super(location, token, jobEntry, cClass);
	}

	@Inject(method = "canBuildingCraftStack", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void canBuildingCraftStack(IRequestManager manager, AbstractBuilding building, ItemStack stack, CallbackInfoReturnable<Boolean> cir)
	{
		var level = manager.getColony().getWorld();

		if (level.isClientSide)
		{
			cir.setReturnValue(false);
			return;
		}

		var modules = building.getModulesByType(CraftingWorkerBuildingModule.class).stream().filter(module -> module.getJobEntry() == this.getJobEntry()).toList();

		for (var module : modules)
		{
			if (module.getAssignedCitizen().stream().anyMatch(c -> c.getJob() instanceof AbstractJobCrafter))
			{
				cir.setReturnValue(true);
				return;
			}

		}

		cir.setReturnValue(false);
	}

}
