package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.minecolonies.core.research.ResearchEffectCategory;

import steve_gall.minecolonies_tweaks.core.common.research.ResearchEffectCategoryExtension;

@Mixin(value = ResearchEffectCategory.class, remap = false)
public abstract class ResearchEffectCategoryMixin implements ResearchEffectCategoryExtension
{
	@Unique
	private String minecolonies_tweaks$command = null;
	private boolean minecolonies_tweaks$isOfflineRunnable = false;

	@Override
	public @Nullable String minecolonies_tweaks$getCommand()
	{
		return this.minecolonies_tweaks$command;
	}

	@Override
	public void minecolonies_tweaks$setCommand(@Nullable String command)
	{
		this.minecolonies_tweaks$command = command;
	}

	@Override
	public boolean minecolonies_tweaks$isOfflineRunnable()
	{
		return this.minecolonies_tweaks$isOfflineRunnable;
	}

	@Override
	public void minecolonies_tweaks$setOfflineRunnable(boolean offlineRunnable)
	{
		this.minecolonies_tweaks$isOfflineRunnable = offlineRunnable;
	}

}
