package steve_gall.minecolonies_tweaks.api.common.building.module;

import java.util.Arrays;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.buildings.modules.IBuildingModuleView;

import net.minecraft.resources.ResourceLocation;

public interface IIdListModuleView extends IBuildingModuleView
{
	default void addId(@NotNull ResourceLocation id)
	{
		this.addIds(Arrays.asList(id));
	}

	void addIds(@NotNull Collection<ResourceLocation> id);

	boolean containsId(@NotNull ResourceLocation id);

	default boolean removeId(@NotNull ResourceLocation id)
	{
		return this.removeIds(Arrays.asList(id));
	}

	boolean removeIds(@NotNull Collection<ResourceLocation> ids);

	void clearIds();

	@NotNull
	String getListId();
}
