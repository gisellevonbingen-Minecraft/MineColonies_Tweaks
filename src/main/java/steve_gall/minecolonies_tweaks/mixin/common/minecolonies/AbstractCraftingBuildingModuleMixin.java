package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
import com.minecolonies.core.colony.buildings.modules.CraftingWorkerBuildingModule;

@Mixin(value = AbstractCraftingBuildingModule.class, remap = false)
public abstract class AbstractCraftingBuildingModuleMixin
{
	@Shadow
	private JobEntry jobEntry;
	@Shadow
	private AbstractBuilding building;

	@Inject(method = "improveRecipe", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private void improveRecipe(IRecipeStorage recipe, int count, ICitizenData citizen, CallbackInfo ci)
	{
		if (this.building.getModulesByType(CraftingWorkerBuildingModule.class).stream().filter(module -> module.getJobEntry() == this.jobEntry).findAny().isEmpty())
		{
			ci.cancel();
		}

	}

}
