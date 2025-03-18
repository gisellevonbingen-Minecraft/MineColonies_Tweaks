package steve_gall.minecolonies_tweaks.core.common.research;

import org.jetbrains.annotations.Nullable;

public interface GlobalResearchEffectExtension
{
	void minecolonies_tweaks$setCommand(@Nullable String command);

	@Nullable
	String minecolonies_tweaks$getCommand();

	void minecolonies_tweaks$setOfflineRunnable(boolean offlineRunnable);

	boolean minecolonies_tweaks$isOfflineRunnable();
}
