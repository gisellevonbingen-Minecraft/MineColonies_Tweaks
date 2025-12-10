package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ldtteam.blockui.Color;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.ScrollingList;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.research.util.ResearchConstants;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.WindowConstants;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.client.gui.WindowResearchTree;
import com.minecolonies.core.client.gui.modules.building.UniversityModuleWindow;
import com.minecolonies.core.colony.buildings.moduleviews.UniversityResearchModuleView;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class UniversityDashboardWindow extends AbstractWindowSkeleton
{
	private final IBuildingView buildingView;
	private final UniversityModuleWindow fake;

	public UniversityDashboardWindow(UniversityResearchModuleView moduleView)
	{
		super(new ResourceLocation(Constants.MOD_ID, "gui/layouthuts/layoutuniversity.xml"));
		this.buildingView = moduleView.getBuildingView();
		this.fake = new UniversityModuleWindow(moduleView)
		{
			@Override
			public void updateResearchCount(int offset)
			{
				UniversityDashboardWindow.this.updateResearchCount(offset);
			}

			@Override
			public void open()
			{
				UniversityDashboardWindow.this.close();
				UniversityDashboardWindow.this.open();
			}
		};

		var branches = IGlobalResearchTree.getInstance().getBranches();
		branches.sort(Comparator.comparingInt(branchId -> IGlobalResearchTree.getInstance().getBranchData(branchId).getSortOrder()));

		var visibleBranches = new ArrayList<ResourceLocation>();
		var allRequirements = new ArrayList<List<MutableComponent>>();

		for (var branch : branches)
		{
			var requirements = this.getHidingRequirementDesc(branch);

			if (requirements.isEmpty() || !IGlobalResearchTree.getInstance().getBranchData(branch).getHidden())
			{
				visibleBranches.add(branch);
				allRequirements.add(requirements);
			}

		}

		var researchList = this.findPaneOfTypeByID("researches", ScrollingList.class);
		researchList.setDataProvider(new ResearchListProvider(visibleBranches, allRequirements));
		updateResearchCount(0);
	}

	public List<MutableComponent> getHidingRequirementDesc(ResourceLocation branch)
	{
		var branchRequirements = new ArrayList<MutableComponent>();
		var researchTree = IGlobalResearchTree.getInstance();

		for (var primaryResearchId : researchTree.getPrimaryResearch(branch))
		{
			var research = researchTree.getResearch(branch, primaryResearchId);
			var researchRequirements = research.getResearchRequirements();

			if (!research.isHidden() || researchTree.isResearchRequirementsFulfilled(researchRequirements, this.buildingView.getColony()))
			{
				return Collections.emptyList();
			}
			else
			{
				if (branchRequirements.isEmpty())
				{
					branchRequirements.add(Component.translatable("com.minecolonies.coremod.research.locked"));
				}
				else
				{
					branchRequirements.add(Component.translatable("Or").setStyle((Style.EMPTY).withColor(ChatFormatting.BLUE)));
				}

				for (var requirement : researchRequirements)
				{
					if (!requirement.isFulfilled(this.buildingView.getColony()))
					{
						branchRequirements.add(Component.literal("-").append(requirement.getDesc().setStyle((Style.EMPTY).withColor(ChatFormatting.RED))));
					}
					else
					{
						branchRequirements.add(Component.literal("-").append(requirement.getDesc().setStyle((Style.EMPTY).withColor(ChatFormatting.AQUA))));
					}

				}

			}

		}

		return branchRequirements;
	}

	@Override
	public void onButtonClicked(Button button)
	{
		super.onButtonClicked(button);

		if (button.getParent() != null)
		{
			var branchName = button.getParent().getID();

			if (ResourceLocation.isValidResourceLocation(branchName) && IGlobalResearchTree.getInstance().getBranches().contains(new ResourceLocation(branchName)))
			{
				new WindowResearchTree(new ResourceLocation(branchName), this.buildingView, this.fake).open();
			}

		}

	}

	public void updateResearchCount(int offset)
	{
		var researchInProgress = this.buildingView.getColony().getResearchManager().getResearchTree().getResearchInProgress().size() + offset;
		this.findPaneOfTypeByID("maxresearchwarn", Text.class).setText(Component.translatable("com.minecolonies.coremod.gui.research.countinprogress", researchInProgress, this.buildingView.getBuildingLevel()));

		if (this.buildingView.getBuildingLevel() <= researchInProgress)
		{
			this.findPaneOfTypeByID("maxresearchwarn", Text.class).setColors(Color.getByName("red", 0));
		}
		else
		{
			this.findPaneOfTypeByID("maxresearchwarn", Text.class).setColors(Color.getByName("black", 0));
		}

	}

	private static class ResearchListProvider implements ScrollingList.DataProvider
	{
		private final List<ResourceLocation> branches;
		private final List<List<MutableComponent>> requirements;

		public ResearchListProvider(List<ResourceLocation> branches, List<List<MutableComponent>> requirements)
		{
			this.branches = branches;
			this.requirements = requirements;
		}

		@Override
		public int getElementCount()
		{
			return this.branches.size();
		}

		@Override
		public void updateElement(int index, Pane rowPane)
		{
			var branchId = this.branches.get(index);
			var branchData = IGlobalResearchTree.getInstance().getBranchData(branchId);
			var requirements = this.requirements.get(index);

			var button = rowPane.findPaneOfTypeByID(WindowConstants.GUI_LIST_ELEMENT_NAME, ButtonImage.class);
			button.getParent().setID(branchId.toString());

			if (requirements.isEmpty())
			{
				button.setText(MutableComponent.create(branchData.getName()));
			}
			else
			{
				button.setText(Component.translatable("----------"));
				button.disable();
			}

			if (button.getHoverPane() == null && (!requirements.isEmpty() || !branchData.getSubtitle().getKey().isEmpty()))
			{
				var hoverText = PaneBuilders.tooltipBuilder().hoverPane(button);

				if (!branchData.getSubtitle().getKey().isEmpty())
				{
					hoverText.append(MutableComponent.create(branchData.getSubtitle())).colorName("GRAY").paragraphBreak();
				}

				if (!requirements.isEmpty())
				{
					for (var requirement : requirements)
					{
						hoverText.append(requirement).color(ResearchConstants.COLOR_TEXT_UNFULFILLED).paragraphBreak();
					}

				}

				hoverText.build();
			}

		}

	}

}
