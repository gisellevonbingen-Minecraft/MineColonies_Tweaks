package steve_gall.minecolonies_tweaks.api.common.building.module;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import steve_gall.minecolonies_tweaks.core.common.network.message.AssignIdListMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.AssignIdListMessage.Function;

public abstract class AbstractIdListModuleView extends AbstractBuildingModuleView implements IIdListModuleView
{
	private String listId;
	private final Set<ResourceLocation> ids;

	public AbstractIdListModuleView()
	{
		this.listId = "";
		this.ids = new HashSet<>();
	}

	@Override
	public void deserialize(@NotNull RegistryFriendlyByteBuf buf)
	{
		this.listId = buf.readUtf();

		this.ids.clear();
		this.ids.addAll(buf.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
	}

	@Override
	public boolean addIds(@NotNull Collection<ResourceLocation> ids)
	{
		PacketDistributor.sendToServer(new AssignIdListMessage(this, Function.ADD, ids));
		return this.ids.addAll(ids);
	}

	@Override
	public boolean containsId(@NotNull ResourceLocation id)
	{
		return this.ids.contains(id);
	}

	@Override
	public boolean removeIds(@NotNull Collection<ResourceLocation> ids)
	{
		PacketDistributor.sendToServer(new AssignIdListMessage(this, Function.REMOVE, ids));
		return this.ids.removeAll(ids);
	}

	@Override
	public void clearIds()
	{
		PacketDistributor.sendToServer(new AssignIdListMessage(this, Function.CLEAR, Collections.emptyList()));
		this.ids.clear();
	}

	@Override
	public @NotNull String getListId()
	{
		return this.listId;
	}

}
