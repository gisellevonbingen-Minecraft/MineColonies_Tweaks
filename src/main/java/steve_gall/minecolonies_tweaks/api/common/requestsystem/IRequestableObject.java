package steve_gall.minecolonies_tweaks.api.common.requestsystem;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.core.colony.requestsystem.requests.AbstractRequest;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

	@NotNull
	default ResourceLocation getDisplayIcon()
	{
		return AbstractRequest.MISSING;
	}

	@NotNull
	default List<MutableComponent> getResolverToolTip(@NotNull IColonyView colony)
	{
		return Collections.emptyList();
	}

}
