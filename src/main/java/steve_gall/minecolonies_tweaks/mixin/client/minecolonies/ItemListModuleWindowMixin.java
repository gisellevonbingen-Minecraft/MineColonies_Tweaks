package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.View;
import com.minecolonies.api.colony.buildings.modules.IBuildingModuleView;
import com.minecolonies.api.colony.buildings.modules.IItemListModuleView;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.core.client.gui.AbstractModuleWindow;
import com.minecolonies.core.client.gui.modules.building.ItemListModuleWindow;
import com.minecolonies.core.colony.buildings.moduleviews.ItemListModuleView;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import steve_gall.minecolonies_tweaks.core.client.gui.ViewOverrideExtension;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.network.message.AssignFilterableItemsMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.AssignFilterableItemsMessage.Function;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.ItemListModuleViewAccessor;

@Mixin(value = ItemListModuleWindow.class, remap = false)
public abstract class ItemListModuleWindowMixin<T extends IBuildingModuleView> extends AbstractModuleWindow<T> implements ViewOverrideExtension
{
	@Unique
	private static final String minecolonies_tweaks$BUTTON_TOGGLE_IN_CURRENT = "toggleInCurrent";
	@Unique
	private static final String minecolonies_tweaks$BUTTON_RESET_IN_CURRENT = "resetInCurrent";

	@Shadow(remap = false)
	private ScrollingList resourceList;
	@Shadow(remap = false)
	private boolean isInverted;
	@Shadow(remap = false)
	private List<ItemStorage> currentDisplayedList;

	public ItemListModuleWindowMixin(T moduleView, ResourceLocation res)
	{
		super(moduleView, res);
	}

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"))
	private void init(IItemListModuleView moduleView, ResourceLocation res, CallbackInfo ci)
	{
		this.registerButton(minecolonies_tweaks$BUTTON_TOGGLE_IN_CURRENT, this::minecolonies_tweaks$toggleClick);
		this.registerButton(minecolonies_tweaks$BUTTON_RESET_IN_CURRENT, this::minecolonies_tweaks$resetClick);
	}

	private void minecolonies_tweaks$toggleClick(Button button)
	{
		var module = this.moduleView.getBuildingView().getModuleViewMatching(ItemListModuleView.class, view -> view.getId().equals(this.id));
		var list = ((ItemListModuleViewAccessor) module).getListsOfItems();
		var toRemoves = new ArrayList<ItemStorage>();
		var toAdds = new ArrayList<ItemStorage>();

		for (var storage : this.currentDisplayedList)
		{
			if (list.contains(storage))
			{
				toRemoves.add(storage);
				list.remove(storage);
			}
			else
			{
				toAdds.add(storage);
				list.add(storage);
			}

		}

		PacketDistributor.sendToServer(new AssignFilterableItemsMessage(module, Function.REMOVE, toRemoves));
		PacketDistributor.sendToServer(new AssignFilterableItemsMessage(module, Function.ADD, toAdds));
		this.resourceList.refreshElementPanes();
	}

	private void minecolonies_tweaks$resetClick(Button button)
	{
		var module = this.moduleView.getBuildingView().getModuleViewMatching(ItemListModuleView.class, view -> view.getId().equals(this.id));
		var list = ((ItemListModuleViewAccessor) module).getListsOfItems();

		list.removeAll(this.currentDisplayedList);

		PacketDistributor.sendToServer(new AssignFilterableItemsMessage(module, Function.REMOVE, this.currentDisplayedList));
		this.resourceList.refreshElementPanes();
	}

	@Override
	public void minecolonies_tweaks$onParse(View view, PaneParams params)
	{
		Loader.createFromXMLFile(MineColoniesTweaks.rl("gui/layouthuts/layoutfilterablelist.xml"), this);
	}

}
