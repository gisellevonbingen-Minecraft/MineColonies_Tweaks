package steve_gall.minecolonies_tweaks.core.client.gui;

import org.jetbrains.annotations.Nullable;

import com.ldtteam.blockui.Pane;
import com.minecolonies.api.colony.buildings.views.IBuildingView;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElement;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingEmptySlotCounter;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingUtils;
import steve_gall.minecolonies_tweaks.core.common.item.ItemInventoryScroll;

public class InventoryScrollElement extends ResourceScrollBookElement
{
	private Component buildingName = EMPTY;
	private Component text1 = EMPTY;
	private Component text2 = EMPTY;

	private BuildingEmptySlotCounter slotCounter;

	public InventoryScrollElement(ItemStack stack)
	{
		super(stack);
	}

	@Override
	public void onOpenClicked()
	{
		super.onOpenClicked();

		ItemInventoryScroll.openWindow(this.stack, null);
	}

	@Override
	public void update()
	{
		super.update();

		var buildingView = this.getBuildingView();

		if (buildingView == null)
		{
			this.buildingName = EMPTY;
			this.text1 = BUILDING_IS_MISSING;
			this.text2 = EMPTY;
			this.slotCounter = null;
			return;
		}

		this.valid = true;
		this.buildingName = Component.empty().append(BuildingUtils.getDisplayName(buildingView)).withStyle(ChatFormatting.DARK_PURPLE);

		if (this.slotCounter == null || this.slotCounter.getBuildingView() != buildingView)
		{
			this.slotCounter = new BuildingEmptySlotCounter(buildingView);
		}

		if (this.slotCounter.update())
		{
			this.text1 = this.slotCounter.getSlotsText();
			this.text2 = Component.literal("(" + this.slotCounter.getPercentText() + ")");
		}

	}

	@Override
	public void update(int index, Pane rowPane)
	{
		super.update(index, rowPane);

		this.getDesc1Label(rowPane).setText(this.buildingName);
		this.getDesc2Label(rowPane).setText(this.text1);
		this.getDesc3Label(rowPane).setText(this.text2);

		if (this.slotCounter != null)
		{
			var tooltip = Component.empty().append(this.text1).append(" ").append(this.text2);
			this.getTooltip2(rowPane).setText(tooltip);
			this.getTooltip3(rowPane).setText(tooltip);
		}

	}

	@Nullable
	public IBuildingView getBuildingView()
	{
		var pos = ItemInventoryScroll.getPos(this.stack);
		return pos != null ? pos.getBuildingView() : null;
	}

}
