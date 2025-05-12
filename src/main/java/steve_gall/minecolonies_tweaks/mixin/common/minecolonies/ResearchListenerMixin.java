package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.Collection;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonElement;
import com.minecolonies.core.datalistener.ResearchListener;
import com.minecolonies.core.research.GlobalResearch;
import com.minecolonies.core.research.ResearchEffectCategory;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.research.GlobalResearchEffectExtension;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchEffectCategoryExtension;

@Mixin(value = ResearchListener.class, remap = false)
public abstract class ResearchListenerMixin
{
	private static final String RESEARCH_COMMAND_PROP = "command";
	private static final String RESEARCH_OFFLINE_RUNNABLE_PROP = "offlineRunnable";

	@Inject(method = "parseResearches", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void parseResearches(Map<ResourceLocation, JsonElement> object, Map<ResourceLocation, ResearchEffectCategory> effectCategories, Collection<ResourceLocation> removeResearches, Collection<ResourceLocation> removeBranches, CallbackInfoReturnable<Map<ResourceLocation, GlobalResearch>> cir)
	{
		var map = cir.getReturnValue();

		for (var entry : map.entrySet())
		{
			var research = entry.getValue();

			for (var effect : research.getEffects())
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

	@Inject(method = "parseResearchEffectCategories", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void parseResearchEffectCategories(Map<ResourceLocation, JsonElement> object, CallbackInfoReturnable<Map<ResourceLocation, ResearchEffectCategory>> cir)
	{
		for (var entry : cir.getReturnValue().entrySet())
		{
			var json = object.get(entry.getKey()).getAsJsonObject();

			if (json.has(RESEARCH_COMMAND_PROP))
			{
				var command = json.get(RESEARCH_COMMAND_PROP).getAsString();
				((ResearchEffectCategoryExtension) entry.getValue()).minecolonies_tweaks$setCommand(command);
			}

			if (json.has(RESEARCH_OFFLINE_RUNNABLE_PROP))
			{
				var offlineRunnable = json.get(RESEARCH_OFFLINE_RUNNABLE_PROP).getAsBoolean();
				((ResearchEffectCategoryExtension) entry.getValue()).minecolonies_tweaks$setOfflineRunnable(offlineRunnable);
			}

		}

	}

}
