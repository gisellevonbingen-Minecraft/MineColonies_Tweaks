package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonObject;
import com.minecolonies.api.research.effects.IResearchEffect;
import com.minecolonies.core.research.GlobalResearch;
import com.minecolonies.core.research.ResearchEffectCategory;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.research.GlobalResearchEffectExtension;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchEffectCategoryExtension;

@Mixin(value = GlobalResearch.class, remap = false)
public abstract class GlobalResearchMixin
{
	@Shadow(remap = false)
	private List<IResearchEffect<?>> effects;

	@Inject(method = "parseEffects", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void parseEffects(JsonObject researchJson, Map<ResourceLocation, ResearchEffectCategory> effectCategories, CallbackInfo ci)
	{
		for (var effect : this.effects)
		{
			if (effect instanceof GlobalResearchEffectExtension effectExtension)
			{
				var category = effectCategories.get(effect.getId());

				if (category instanceof ResearchEffectCategoryExtension categoryExtension)
				{
					effectExtension.minecolonies_tweaks$setCommand(categoryExtension.minecolonies_tweaks$getCommand());
					effectExtension.minecolonies_tweaks$setOfflineRunnable(categoryExtension.minecolonies_tweaks$isOfflineRunnable());
				}

			}

		}

	}

}
