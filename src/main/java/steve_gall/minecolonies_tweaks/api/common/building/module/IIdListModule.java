package steve_gall.minecolonies_tweaks.api.common.building.module;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;

public interface IIdListModule
{
	boolean addId(@NotNull ResourceLocation id);

	boolean addIds(@NotNull Collection<ResourceLocation> ids);

	boolean containsId(@NotNull ResourceLocation id);

	boolean removeId(@NotNull ResourceLocation id);

	boolean removeIds(@NotNull Collection<ResourceLocation> ids);

	void clearIds();

	@NotNull
	Collection<ResourceLocation> getIds();

	@NotNull
	String getListId();
}
