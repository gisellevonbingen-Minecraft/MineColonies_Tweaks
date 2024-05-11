package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.api.common.CustomizableObjectRegistry;

public class DeliverableObjectRegistry extends CustomizableObjectRegistry<IDeliverableObject>
{
	public static final DeliverableObjectRegistry INSTANCE = new DeliverableObjectRegistry();

	@Override
	protected ResourceLocation getId(IDeliverableObject object)
	{
		return object.getId();
	}

}
