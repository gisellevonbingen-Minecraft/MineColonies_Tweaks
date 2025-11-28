package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.ScrollingList;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.WindowConstants;
import com.minecolonies.core.client.gui.AbstractModuleWindow;
import com.minecolonies.core.client.gui.WindowSelectRes;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.api.common.building.module.IMaximumStockEntry;
import steve_gall.minecolonies_tweaks.api.common.building.module.IMaximumStockModuleView;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class MaximumStockModuleWindow extends AbstractModuleWindow
{
	private static final String LABEL_ADD = "com.minecolonies.coremod.gui.warehouse.add";
	private static final String LABEL_LIMIT_REACHED = "com.minecolonies.coremod.gui.warehouse.limitreached";

	private final ScrollingList resourceList;

	private final IMaximumStockModuleView moduleView;
	private final List<IMaximumStockEntry> entries;

	private Button confirmButton;

	public MaximumStockModuleWindow(IBuildingView building, IMaximumStockModuleView moduleView)
	{
		super(building, MineColoniesTweaks.rl("gui/layouthuts/layoutmaximumstock.xml").toString());

		this.moduleView = moduleView;
		this.entries = new ArrayList<>();

		this.resourceList = this.window.findPaneOfTypeByID("resourcesstock", ScrollingList.class);
		this.resourceList.setDataProvider(new ScrollingList.DataProvider()
		{
			@Override
			public int getElementCount()
			{
				return entries.size();
			}

			@Override
			public void updateElement(int index, Pane rowPane)
			{
				var entry = entries.get(index);
				var resource = entry.stack();
				resource.setCount(resource.getMaxStackSize());

				rowPane.findPaneOfTypeByID(WindowConstants.RESOURCE_NAME, Text.class).setText(resource.getHoverName());
				rowPane.findPaneOfTypeByID(WindowConstants.QUANTITY_LABEL, Text.class).setText(Component.literal(String.valueOf(entry.quantity())));
				rowPane.findPaneOfTypeByID(WindowConstants.RESOURCE_ICON, ItemIcon.class).setItem(resource);
			}
		});

	}

	@Override
	public void onButtonClicked(@NotNull Button button)
	{
		super.onButtonClicked(button);

		var confirmButton = this.confirmButton;

		if (confirmButton != null)
		{
			confirmButton.setText(Component.literal("X"));
			this.confirmButton = null;
		}

		if (button.getID().equals(WindowConstants.STOCK_REMOVE))
		{
			if (confirmButton == button)
			{
				var row = this.resourceList.getListElementIndexByPane(button);
				var entry = this.entries.get(row);

				this.moduleView.removeMaximumStock(entry.stack());
				this.updateStockList();
			}
			else
			{
				this.confirmButton = button;
				this.confirmButton.setText(Component.translatable("minecolonies_tweaks.gui.surely"));
			}

		}
		else if (button.getID().equals(WindowConstants.STOCK_ADD))
		{
			if (!this.moduleView.hasMaximumStockReachedLimit())
			{
				new WindowSelectRes(this, (stack) -> true, (stack, qty) ->
				{
					this.moduleView.addMaximumStock(stack, qty);
					this.updateStockList();
				}, true).open();
			}

		}

	}

	@Override
	public void onOpened()
	{
		super.onOpened();

		this.updateStockList();
	}

	private void updateStockList()
	{
		this.entries.clear();
		this.entries.addAll(this.moduleView.getMaximumStocks());

		var button = this.findPaneOfTypeByID(WindowConstants.STOCK_ADD, ButtonImage.class);

		if (this.moduleView.hasMaximumStockReachedLimit())
		{
			button.setText(Component.translatable(LABEL_LIMIT_REACHED));
			button.setImage(new ResourceLocation(Constants.MOD_ID, "textures/gui/builderhut/builder_button_medium_disabled.png"), false);
		}
		else
		{
			button.setText(Component.translatable(LABEL_ADD));
			button.setImage(new ResourceLocation(Constants.MOD_ID, "textures/gui/builderhut/builder_button_medium.png"), false);
		}

		var kindsText = this.findPaneOfTypeByID("kinds", Text.class);
		kindsText.setText(Component.literal(this.entries.size() + "/" + this.moduleView.getMaximumStockLimit()));
	}

}
