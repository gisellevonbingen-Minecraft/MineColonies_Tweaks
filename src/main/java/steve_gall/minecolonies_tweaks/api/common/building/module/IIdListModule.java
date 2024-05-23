package steve_gall.minecolonies_tweaks.api.common.building.module;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;

public interface IIdListModule
{
	void addId(@NotNull ResourceLocation id);

	boolean containsId(@NotNull ResourceLocation id);

	boolean removeId(@NotNull ResourceLocation id);

	void clearIds();

	@NotNull
	Collection<ResourceLocation> getIds();

	@NotNull
	String getListId();
}
