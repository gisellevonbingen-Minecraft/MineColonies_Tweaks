package steve_gall.minecolonies_tweaks.core.client.gui;

import org.jetbrains.annotations.Nullable;

import com.ldtteam.blockui.Pane;
import com.minecolonies.api.colony.buildings.views.IBuildingView;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElement;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingUtils;
import steve_gall.minecolonies_tweaks.core.common.item.ItemBuildingLinkScroll;

public class UniversityScrollElement extends ResourceScrollBookElement
{
	private Component buildingName = EMPTY;
	private Component text1 = EMPTY;
	private Component text2 = EMPTY;

	public UniversityScrollElement(ItemStack stack)
	{
		super(stack);
	}

	@Override
	public void onOpenClicked()
	{
		super.onOpenClicked();

		((ItemBuildingLinkScroll) this.stack.getItem()).openWindow(this.stack, null);
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
			return;
		}

		this.valid = true;

		this.buildingName = Component.empty().append(BuildingUtils.getDisplayName(buildingView)).withStyle(ChatFormatting.DARK_PURPLE);

		var researchInProgress = buildingView.getColony().getResearchManager().getResearchTree().getResearchInProgress().size();
		var limit = buildingView.getBuildingLevel();
		this.text1 = Component.translatable("com.minecolonies.coremod.gui.research.countinprogress", researchInProgress, limit).withStyle(limit <= researchInProgress ? ChatFormatting.RED : ChatFormatting.BLACK);
		this.text2 = EMPTY;
	}

	@Override
	public void update(int index, Pane rowPane)
	{
		super.update(index, rowPane);

		this.getDesc1Label(rowPane).setText(this.buildingName);
		this.getDesc2Label(rowPane).setText(this.text1);
		this.getDesc3Label(rowPane).setText(this.text2);
	}

	@Nullable
	public IBuildingView getBuildingView()
	{
		var pos = ItemBuildingLinkScroll.getPos(this.stack);
		return pos != null ? pos.getBuildingView() : null;
	}

}
