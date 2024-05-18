package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.requestsystem.resolvers.PrivateWorkerCraftingRequestResolver;
import com.minecolonies.core.colony.requestsystem.resolvers.core.AbstractCraftingRequestResolver;

import steve_gall.minecolonies_tweaks.api.common.crafting.ICustomizableRecipeStorage;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingHelper;

@Mixin(value = PrivateWorkerCraftingRequestResolver.class, remap = false)
public abstract class PrivateWorkerCraftingRequestResolverMixin extends AbstractCraftingRequestResolver
{
	public PrivateWorkerCraftingRequestResolverMixin(@NotNull ILocation location, @NotNull IToken<?> token, @NotNull JobEntry entry, boolean isPublicCrafter)
	{
		super(location, token, entry, isPublicCrafter);
	}

	@Inject(method = "canBuildingCraftRecipe", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void canBuildingCraftRecipe(AbstractBuilding building, IRecipeStorage recipeStorage, CallbackInfoReturnable<Boolean> cir)
	{
		if (recipeStorage instanceof ICustomizableRecipeStorage)
		{
			cir.setReturnValue(BuildingHelper.getCraftableModule(building, recipeStorage.getToken(), this.getJobEntry()) != null);
		}

	}

}
