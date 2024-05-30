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
	private final Consumer<CustomToolType> register;

	public CustomToolTypeRegisterEvent(@NotNull Consumer<CustomToolType> register)
	{
		this.register = register;
	}

	public void register(@NotNull CustomToolType data)
	{
		this.register.accept(data);
	}

}
