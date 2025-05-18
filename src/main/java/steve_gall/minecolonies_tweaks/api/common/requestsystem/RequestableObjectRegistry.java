package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.api.common.SimpleObjectRegistry;

public class RequestableObjectRegistry extends SimpleObjectRegistry<IRequestableObject>
{
	public static final RequestableObjectRegistry INSTANCE = new RequestableObjectRegistry();

	@Override
	protected ResourceLocation getId(IRequestableObject object)
	{
		return object.getId();
	}

}
