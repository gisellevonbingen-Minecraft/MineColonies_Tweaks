package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * {@link MinecraftForge#EVENT_BUS}
 */
public class CustomToolTypeRegisterEvent extends Event
{
	private final Consumer<CustomToolTypeData> register;

	public CustomToolTypeRegisterEvent(@NotNull Consumer<CustomToolTypeData> register)
	{
		this.register = register;
	}

	public void register(@NotNull CustomToolTypeData data)
	{
		this.register.accept(data);
	}

}
