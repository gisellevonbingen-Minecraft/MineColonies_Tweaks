package steve_gall.minecolonies_tweaks.api.common.building.module;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.buildings.modules.IBuildingModuleView;

import net.minecraft.resources.ResourceLocation;

public interface IIdListModuleView extends IBuildingModuleView
{
	default boolean addId(@NotNull ResourceLocation id)
	{
		return this.addIds(Collections.singletonList(id));
	}

	boolean addIds(@NotNull Collection<ResourceLocation> ids);

	boolean containsId(@NotNull ResourceLocation id);

	default boolean removeId(@NotNull ResourceLocation id)
	{
		return this.removeIds(Collections.singletonList(id));
	}

	boolean removeIds(@NotNull Collection<ResourceLocation> ids);

	void clearIds();

	@NotNull
	String getListId();
}
