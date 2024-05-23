package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
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
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.core.client.gui.AbstractModuleWindow;
import com.minecolonies.core.client.gui.modules.ItemListModuleWindow;
import com.minecolonies.core.colony.buildings.moduleviews.ItemListModuleView;

import steve_gall.minecolonies_tweaks.core.client.gui.ViewOverrideExtension;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.network.message.AssignFilterableItemsMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.AssignFilterableItemsMessage.Function;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.ItemListModuleViewAccessor;

@Mixin(value = ItemListModuleWindow.class, remap = false)
public abstract class ItemListModuleWindowMixin extends AbstractModuleWindow implements ViewOverrideExtension
{
	@Unique
	private static final String minecolonies_tweaks$BUTTON_TOGGLE_IN_CURRENT = "toggleInCurrent";
	@Unique
	private static final String minecolonies_tweaks$BUTTON_RESET_IN_CURRENT = "resetInCurrent";

	@Shadow(remap = false)
	private ScrollingList resourceList;
	@Shadow(remap = false)
	protected IBuildingView building;
	@Shadow(remap = false)
	private boolean isInverted;
	@Shadow(remap = false)
	private List<ItemStorage> currentDisplayedList;

	public ItemListModuleWindowMixin(IBuildingView building, String res)
	{
		super(building, res);
	}

	@Inject(method = "onButtonClicked", remap = false, at = @At(value = "TAIL"))
	private void onButtonClicked(@NotNull Button button, CallbackInfo ci)
	{
		var buttonId = button.getID();

		if (Objects.equals(buttonId, minecolonies_tweaks$BUTTON_TOGGLE_IN_CURRENT))
		{
			var module = this.building.getModuleViewMatching(ItemListModuleView.class, view -> view.getId().equals(this.id));
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

			MineColoniesTweaks.network().sendToServer(new AssignFilterableItemsMessage(module, Function.REMOVE, toRemoves));
			MineColoniesTweaks.network().sendToServer(new AssignFilterableItemsMessage(module, Function.ADD, toAdds));
			this.resourceList.refreshElementPanes();
		}
		else if (Objects.equals(buttonId, minecolonies_tweaks$BUTTON_RESET_IN_CURRENT))
		{
			var module = this.building.getModuleViewMatching(ItemListModuleView.class, view -> view.getId().equals(this.id));
			var list = ((ItemListModuleViewAccessor) module).getListsOfItems();

			list.removeAll(this.currentDisplayedList);

			MineColoniesTweaks.network().sendToServer(new AssignFilterableItemsMessage(module, Function.REMOVE, this.currentDisplayedList));
			this.resourceList.refreshElementPanes();
		}

	}

	@Override
	public void minecolonies_tweaks$onParse(View view, PaneParams params)
	{
		Loader.createFromXMLFile(MineColoniesTweaks.rl("gui/layouthuts/layoutfilterablelist.xml"), this);
	}

}
