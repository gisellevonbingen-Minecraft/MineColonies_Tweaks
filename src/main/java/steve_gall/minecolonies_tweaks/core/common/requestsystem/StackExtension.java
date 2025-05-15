package steve_gall.minecolonies_tweaks.core.common.requestsystem;

import java.util.UUID;

public interface StackExtension
{
	void minecolonies_tweaks$setRequester(UUID uuid);

	UUID minecolonies_tweaks$getRequester();
}
