package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.requestsystem.requestable.IRequestable;

public interface ICustomizableRequestable extends IRequestable
{
	@NotNull
	IRequestableObject getObject();
}
