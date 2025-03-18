package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonElement;
import com.minecolonies.core.datalistener.ResearchListener;
import com.minecolonies.core.research.ResearchEffectCategory;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchEffectCategoryExtension;

@Mixin(value = ResearchListener.class, remap = false)
public abstract class ResearchListenerMixin
{
	private static final String RESEARCH_COMMAND_PROP = "command";
	private static final String RESEARCH_OFFLINE_RUNNABLE_PROP = "offlineRunnable";

	@Inject(method = "parseResearchEffects", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void parseResearchEffects(Map<ResourceLocation, JsonElement> object, CallbackInfoReturnable<Map<ResourceLocation, ResearchEffectCategory>> cir)
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
