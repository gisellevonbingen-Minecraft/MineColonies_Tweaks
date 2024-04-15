package steve_gall.minecolonies_tweaks.api.common.util;

import java.util.function.Predicate;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;

public class EventBusHelper
{
	public static <EVENT extends Event> boolean postUntil(IEventBus bus, EVENT event, Predicate<EVENT> predicate)
	{
		return bus.post(event, (listener, e) ->
		{
			if (predicate.test(event))
			{
				listener.invoke(e);
			}

		});

	}

}
