package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.requestsystem.requestable.IRequestable;

import net.minecraft.resources.ResourceLocation;

public interface ICustomizableRequestable extends IRequestable
{
	@NotNull
	ResourceLocation getId();

	@NotNull
	IRequestableObject getObject();
}
