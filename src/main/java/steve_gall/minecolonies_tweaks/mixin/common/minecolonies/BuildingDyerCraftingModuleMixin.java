package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import com.llamalad7.mixinextras.sugar.Local;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingDyer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = BuildingDyer.CraftingModule.class, remap = false)
public abstract class BuildingDyerCraftingModuleMixin {

    // We have to inject inside the "if" because using the super call as target is not allowed...
    @Inject(method = "getFirstRecipe", remap = false, at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z", remap = false), cancellable = true)
    void getFirstRecipeIgnoreBleach(CallbackInfoReturnable<IRecipeStorage> cir, @Local IRecipeStorage recipe) {
        if (MCTweaksConfigServer.INSTANCE.jobs.dyerDisableBleaching.get()) {
            cir.setReturnValue(recipe);
        }
    }
}