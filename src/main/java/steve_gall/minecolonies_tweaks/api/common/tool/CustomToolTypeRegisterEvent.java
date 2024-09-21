package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * {@link FMLJavaModLoadingContext.get().getModEventBus()}
 */
public class CustomToolTypeRegisterEvent extends Event implements IModBusEvent
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
