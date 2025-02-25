package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.controls.Tooltip;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.ScrollingList.DataProvider;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.api.util.constant.WindowConstants;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.colony.buildings.moduleviews.BuildingResourcesModuleView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBuilder;
import com.minecolonies.core.items.ItemClipboard;
import com.minecolonies.core.items.ItemResourceScroll;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.ItemClipboardAccessor;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.ItemResourceScrollAccessor;

public class ResourceScrollBookListWindow extends AbstractWindowSkeleton
{
	public static final Component EMPTY = Component.empty();
	public static final Component BUILDER_NOT_SETTED = Component.translatable("minecolonies_tweaks.gui.resourcebook.builder_not_setted");
	public static final Component COLONY_NOT_SETTED = Component.translatable("minecolonies_tweaks.gui.resourcebook.colony_not_setted");

	public static final String OPEN = "open";
	public static final String DESC1 = "desc1";
	public static final String DESC2 = "desc2";

	private final List<Element> elements;
	private final ScrollingList resourceList;

	public ResourceScrollBookListWindow(List<ItemStack> stacks, @Nullable BOWindow parent)
	{
		super(MineColoniesTweaks.rl("gui/resourcescroll_book_list_window.xml").toString(), null);

		this.elements = new ArrayList<>();

		for (var stack : stacks)
		{
			if (stack.isEmpty())
			{
				continue;
			}
			else if (stack.getItem() instanceof ItemResourceScroll)
			{
				this.elements.add(new Resourcescroll(stack));
			}
			else if (stack.is(ModItems.clipboard))
			{
				this.elements.add(new Clipboard(stack));
			}

		}

		this.resourceList = this.findPaneOfTypeByID(WindowConstants.LIST_RESOURCES, ScrollingList.class);
		this.resourceList.setDataProvider(new DataProvider()
		{
			@Override
			public void updateElement(int index, Pane rowPane)
			{
				elements.get(index).update(index, rowPane);
			}

			@Override
			public int getElementCount()
			{
				return elements.size();
			}
		});
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		for (var element : this.elements)
		{
			element.update();
		}

	}

	@Override
	public void onButtonClicked(@NotNull Button button)
	{
		super.onButtonClicked(button);

		if (button.getID().equals(OPEN))
		{
			var index = this.resourceList.getListElementIndexByPane(button);
			var element = this.elements.get(index);
			element.onOpenClicked();
		}

	}

	@Override
	public boolean onKeyTyped(char ch, int key)
	{
		var index = -1;

		if (InputConstants.KEY_0 == key)
		{
			index = 8;
		}
		else if (InputConstants.KEY_1 <= key && key <= InputConstants.KEY_9)
		{
			index = key - InputConstants.KEY_1;
		}

		if (index > -1 && index < this.elements.size())
		{
			this.elements.get(index).onOpenClicked();
		}

		return super.onKeyTyped(ch, key);
	}

	public class Element
	{
		public final ItemStack stack;

		protected boolean valid = false;

		public Element(ItemStack stack)
		{
			this.stack = stack;
		}

		public void onOpenClicked()
		{

		}

		public void update()
		{
			this.valid = false;
		}

		public void update(int index, Pane rowPane)
		{
			var resourceIcon = rowPane.findPaneOfTypeByID(WindowConstants.RESOURCE_ICON, ItemIcon.class);
			resourceIcon.setItem(this.stack);

			var workerNameLabel = rowPane.findPaneOfTypeByID(WindowConstants.LABEL_WORKERNAME, Text.class);
			workerNameLabel.setText(EMPTY);

			var desc1Label = rowPane.findPaneOfTypeByID(DESC1, Text.class);
			desc1Label.setText(EMPTY);

			var desc2Label = rowPane.findPaneOfTypeByID(DESC2, Text.class);
			desc2Label.setText(EMPTY);

			var openButton = rowPane.findPaneOfTypeByID(OPEN, Button.class);
			openButton.setVisible(this.valid);

			var hoverPane = desc1Label.getHoverPane();

			if (hoverPane == null)
			{
				PaneBuilders.tooltipBuilder().hoverPane(desc1Label).build();
			}
			else
			{
				hoverPane.setVisible(false);
			}

		}

	}

	public class Resourcescroll extends Element
	{
		private Component workerName = EMPTY;
		private Component constructionName = EMPTY;
		private Component progress = EMPTY;

		public Resourcescroll(ItemStack stack)
		{
			super(stack);
		}

		@Override
		public void onOpenClicked()
		{
			super.onOpenClicked();

			var compound = this.stack.getOrCreateTag();
			var player = mc.player;
			ItemResourceScrollAccessor.invokeOpenWindow(compound, player);
		}

		@Override
		public void update()
		{
			super.update();

			var buildingView = this.getBuildingView();

			if (buildingView == null)
			{
				this.workerName = EMPTY;
				this.constructionName = BUILDER_NOT_SETTED;
				this.progress = EMPTY;
				return;
			}

			this.valid = true;
			this.workerName = Component.literal(buildingView.getWorkerName()).withStyle(ChatFormatting.DARK_PURPLE);

			var resourceView = buildingView.getModuleViewByType(BuildingResourcesModuleView.class);
			var workOrderView = buildingView.getColony().getWorkOrder(resourceView.getWorkOrderId());

			if (workOrderView != null)
			{
				this.constructionName = Component.literal(workOrderView.getDisplayName().getString().replace("\n", " "));
			}
			else
			{
				this.constructionName = Component.empty();
			}

			double supplied = 0.0D;
			double total = 0.0D;

			for (var resource : resourceView.getResources().values())
			{
				supplied += Math.min(resource.getAvailable(), resource.getAmount());
				total += resource.getAmount();
			}

			if (total > 0.0D)
			{
				this.progress = Component.translatable("com.minecolonies.coremod.gui.progress.res", (int) ((supplied / total) * 100) + "%", resourceView.getProgress() + "%");
			}
			else
			{
				this.progress = EMPTY;
			}

		}

		@Override
		public void update(int index, Pane rowPane)
		{
			super.update(index, rowPane);

			var workerNameLabel = rowPane.findPaneOfTypeByID(WindowConstants.LABEL_WORKERNAME, Text.class);
			workerNameLabel.setText(this.workerName);

			var desc1Label = rowPane.findPaneOfTypeByID(DESC1, Text.class);
			desc1Label.setText(this.constructionName);

			var desc2Label = rowPane.findPaneOfTypeByID(DESC2, Text.class);
			desc2Label.setText(this.progress);

			if (desc1Label.getHoverPane() instanceof Tooltip tooltip)
			{
				tooltip.setText(this.constructionName);
			}

		}

		public BuildingBuilder.View getBuildingView()
		{
			var compound = this.stack.getTag();

			if (compound != null)
			{
				var colonyId = compound.getInt(NbtTagConstants.TAG_COLONY_ID);
				var builderPos = compound.contains(NbtTagConstants.TAG_BUILDER) ? BlockPosUtil.read(compound, NbtTagConstants.TAG_BUILDER) : null;

				if (builderPos != null)
				{
					var mc = Minecraft.getInstance();
					var colonyView = IColonyManager.getInstance().getColonyView(colonyId, mc.level.dimension());

					if (colonyView != null && colonyView.getBuilding(builderPos) instanceof BuildingBuilder.View buildingView)
					{
						return buildingView;
					}

				}

			}

			return null;
		}

	}

	public class Clipboard extends Element
	{
		private Component colonyName = EMPTY;
		private Component requestCount = EMPTY;

		public Clipboard(ItemStack stack)
		{
			super(stack);
		}

		@Override
		public void onOpenClicked()
		{
			super.onOpenClicked();

			var compound = this.stack.getOrCreateTag();
			var player = mc.player;
			ItemClipboardAccessor.invokeOpenWindow(compound, player.level(), player);
		}

		@Override
		public void update()
		{
			super.update();

			var colonyView = this.getColonyView();

			if (colonyView == null)
			{
				this.colonyName = EMPTY;
				this.requestCount = COLONY_NOT_SETTED;
				return;
			}

			this.valid = true;
			this.colonyName = Component.literal(colonyView.getName()).withStyle(ChatFormatting.DARK_PURPLE);

			var requestCount = 0;

			for (var buildingView : colonyView.getBuildings())
			{
				requestCount += buildingView.getOpenRequestsOfBuilding().size();
			}

			this.requestCount = Component.translatable("minecolonies_tweaks.gui.resourcebook.open_requests", requestCount);
		}

		@Override
		public void update(int index, Pane rowPane)
		{
			super.update(index, rowPane);

			var workerNameLabel = rowPane.findPaneOfTypeByID(WindowConstants.LABEL_WORKERNAME, Text.class);
			workerNameLabel.setText(this.colonyName);

			var desc1Label = rowPane.findPaneOfTypeByID(DESC1, Text.class);
			desc1Label.setText(this.requestCount);
		}

		public IColonyView getColonyView()
		{
			var compound = this.stack.getTag();

			if (compound != null && compound.contains(ItemClipboard.TAG_COLONY))
			{
				var colonyId = compound.getInt(ItemClipboard.TAG_COLONY);
				var mc = Minecraft.getInstance();
				return IColonyManager.getInstance().getColonyView(colonyId, mc.level.dimension());
			}

			return null;
		}

	}

}
