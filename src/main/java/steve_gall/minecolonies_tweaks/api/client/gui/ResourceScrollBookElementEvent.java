package steve_gall.minecolonies_tweaks.api.client.gui;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/**
 * {@link NeoForge#EVENT_BUS}
 */
public class ResourceScrollBookElementEvent extends Event
{
	@NotNull
	private final ItemStack stack;
	@NotNull
	private final Consumer<ResourceScrollBookElement> register;

	public ResourceScrollBookElementEvent(@NotNull ItemStack stack, @NotNull Consumer<ResourceScrollBookElement> register)
	{
		this.stack = stack;
		this.register = register;
	}

	public void register(@NotNull ResourceScrollBookElement element)
	{
		this.register.accept(element);
	}

	@NotNull
	public ItemStack getStack()
	{
		return this.stack;
	}

}
