package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IRequestableObject
{
	@NotNull
	ResourceLocation getId();

	@NotNull
	Component getShortDisplayString();

	@NotNull
	default Component getLongDisplayString()
	{
		return this.getShortDisplayString();
	}

	@NotNull
	List<ItemStack> getDisplayStacks();
}
