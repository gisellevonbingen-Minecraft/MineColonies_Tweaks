package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.controls.TextField;
import com.ldtteam.blockui.controls.Tooltip;
import com.ldtteam.blockui.controls.Tooltip.AutomaticTooltip;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.Box;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.structurize.placement.AbstractBlueprintIterator;
import com.ldtteam.structurize.placement.BlockPlacementResult;
import com.ldtteam.structurize.placement.StructurePhasePlacementResult;
import com.ldtteam.structurize.placement.StructurePlacer;
import com.ldtteam.structurize.storage.ClientFutureProcessor;
import com.ldtteam.structurize.storage.StructurePacks;
import com.ldtteam.structurize.util.PlacementSettings;
import com.ldtteam.structurize.util.RotationMirror;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.LoadOnlyStructureHandler;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.WindowConstants;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBuilder;
import com.minecolonies.core.network.messages.server.colony.building.BuildRequestMessage;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingUtils;
import steve_gall.minecolonies_tweaks.core.common.colony.BatchUpgradeData;
import steve_gall.minecolonies_tweaks.core.common.network.message.BatchUpgradeDataLoadMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.BatchUpgradeDataSaveMessage;

@SuppressWarnings("removal")
public class BatchUpgradeBuildingsWindow extends AbstractWindowSkeleton
{
	public static final String LIST_BUILDINGS = "buildings";
	public static final String LIST_BUILDERS = "builders";
	public static final String LIST_UPGRADE_RESOURCES = "upgradeResources";
	public static final String ICON_BUILDING = "buildingIcon";
	public static final String TEXT_BUILDING_NAME = "buildingName";
	public static final String TEXT_BUILDER_NAME = "builderName";
	public static final String TEXT_ASSIGNED_COUNT = "assignedCount";
	public static final String TEXT_DISTANCE_WITH_BUILDING = "distanceWithBuilding";
	public static final String BUTTON_ASSIGN_AUTO = "assignAllAutomatically";
	public static final String BUTTON_ASSGIN_CLEAR = "clearAssignments";
	public static final String BUTTON_MARK_ALL = "markAllDontUpgrade";
	public static final String BUTTON_MARK_CLAR = "clearDontUpgrade";
	public static final String TEXT_SELECTION = "selectionText";

	private final IColonyView colony;
	private final BOWindow parent;

	private final TextField nameField;
	private final ScrollingList buildingList;
	private final ScrollingList builderList;
	private final ScrollingList upgradeResourceList;
	private final Text selectionText;

	private final Set<BlockPos> savedDontUpgrades;

	private final List<BuildingInfo> updatingBuildings;
	private final Map<BlockPos, BuildingInfo> buildings;
	private final List<BuildingInfo> filteredBuildings;
	private final List<BuilderInfo> builders;
	private final List<BuilderInfo> filteredBuilders;
	private final List<ItemStorage> upgradeResources;
	private final Map<BuildingInfo, BuilderInfo> assignments;

	private boolean requested = false;
	private boolean updating = false;
	private int updateProgress = 0;
	private int updateCount = 0;
	private int selectedBuildingIndex = -1;
	private int selectedBuilderIndex = -1;
	private int lastBuildersBuildingIndex = -1;
	private int lastResourcesBuildingIndex = -1;
	private int nameFilterRequested = 0;

	public BatchUpgradeBuildingsWindow(IColonyView colony, @Nullable BOWindow parent)
	{
		super(MineColoniesTweaks.rl("gui/batch_upgrade_buildings_window.xml").toString(), null);
		this.colony = colony;
		this.parent = parent;

		this.nameField = this.window.findPaneOfTypeByID(WindowConstants.INPUT_FILTER, TextField.class);
		this.buildingList = this.window.findPaneOfTypeByID(LIST_BUILDINGS, ScrollingList.class);
		this.builderList = this.window.findPaneOfTypeByID(LIST_BUILDERS, ScrollingList.class);
		this.upgradeResourceList = this.window.findPaneOfTypeByID(LIST_UPGRADE_RESOURCES, ScrollingList.class);
		this.selectionText = this.window.findPaneOfTypeByID(TEXT_SELECTION, Text.class);

		this.savedDontUpgrades = new HashSet<>();

		this.updatingBuildings = new ArrayList<>();
		this.buildings = new HashMap<>();
		this.filteredBuildings = new ArrayList<>();
		this.builders = new ArrayList<>();
		this.filteredBuilders = new ArrayList<>();
		this.upgradeResources = new ArrayList<>();
		this.assignments = new HashMap<>();

		this.nameField.setHandler(this::onFieldInput);
		this.buildingList.setDataProvider(this.filteredBuildings::size, this::updateBuildingRow);
		this.builderList.setDataProvider(this.filteredBuilders::size, this::updateBuilderRow);
		this.upgradeResourceList.setDataProvider(this.upgradeResources::size, this::updateUpgradeResourceRow);
	}

	public void setSavedBuildings(BatchUpgradeData data)
	{
		this.savedDontUpgrades.clear();

		for (var building : data.getMarkAsDontUpgrades())
		{
			this.savedDontUpgrades.add(building);
		}

		if (!this.updating)
		{
			for (var building : this.buildings.values())
			{
				this.applySavedData(building);
			}

			this.updateBuildingList();
			this.onBuildingCountsChanged();
		}

	}

	private void applySavedData(BuildingInfo building)
	{
		var id = building.building.getID();

		if (this.savedDontUpgrades.contains(id))
		{
			this.savedDontUpgrades.remove(id);
			this.markAsDontUpgrade(building);
		}

	}

	@Override
	public void close()
	{
		if (this.parent != null)
		{
			this.parent.open();
			return;
		}

		super.close();
	}

	protected void onFieldInput(TextField input)
	{
		if (input == this.nameField)
		{
			this.nameFilterRequested = 10;
		}

	}

	@Override
	public void onOpened()
	{
		super.onOpened();

		this.requested = true;
		this.nameField.setFocus();

		this.updateBuildingList();
		this.onBuildingCountsChanged();

		MineColoniesTweaks.network().sendToServer(new BatchUpgradeDataLoadMessage(this.colony));
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.updating)
		{
			synchronized (this.updatingBuildings)
			{
				if (this.updateProgress >= this.updateCount)
				{
					this.updating = false;
				}

				for (var building : this.updatingBuildings)
				{
					this.buildings.put(building.building.getID(), building);
					this.applySavedData(building);
				}

				this.updatingBuildings.clear();
			}

			this.nameFilterRequested = 0;
			this.updateBuildingList();
			this.onBuildingCountsChanged();
		}

		if (this.requested && !this.updating)
		{
			this.requested = false;
			this.updating = true;
			this.updateProgress = 0;
			this.updateCount = 0;
			this.buildings.clear();
			this.builders.clear();

			for (var citizen : this.colony.getCitizens().values())
			{
				var jobView = citizen.getJobView();

				if (jobView != null && jobView.getEntry() == ModJobs.builder.get())
				{
					this.builders.add(new BuilderInfo(this.colony, citizen));
				}

			}

			var level = this.mc.level;

			for (var building : this.colony.getBuildings())
			{
				if (building.hasWorkOrder() || building.isBuildingMaxLevel())
				{
					continue;
				}

				var buildingName = building.getStructurePath().replace(".blueprint", "");

				if (buildingName.isEmpty())
				{
					continue;
				}

				this.updateCount++;
				buildingName = buildingName.substring(0, buildingName.length() - 1) + (building.getBuildingLevel() + 1) + ".blueprint";
				ClientFutureProcessor.queueBlueprint(new ClientFutureProcessor.BlueprintProcessingData(StructurePacks.getBlueprintFuture(building.getStructurePack(), buildingName), blueprint ->
				{
					var upgradeResources = new HashMap<ItemStorage, AtomicInteger>();

					if (blueprint != null)
					{
						var buildingRotation = BlockPosUtil.getRotationFromRotations(building.getRotation());
						var buldingMirror = building.isMirrored() ? Mirror.FRONT_BACK : Mirror.NONE;
						blueprint.setRotationMirrorRelative(RotationMirror.of(buildingRotation, buldingMirror), level);

						var placer = new StructurePlacer(new LoadOnlyStructureHandler(level, building.getPosition(), blueprint, new PlacementSettings(), true));
						StructurePhasePlacementResult result;
						var progressPos = AbstractBlueprintIterator.NULL_POS;

						do
						{
							result = placer.executeStructureStep(level, null, progressPos, StructurePlacer.Operation.GET_RES_REQUIREMENTS, () -> placer.getIterator().increment((info, pos, handler) -> false), true);
							progressPos = result.getIteratorPos();

							for (var stack : result.getBlockResult().getRequiredItems())
							{
								var existing = upgradeResources.computeIfAbsent(new ItemStorage(stack), s -> new AtomicInteger());
								existing.addAndGet(stack.getCount());
							}

						}
						while (result != null && result.getBlockResult().getResult() != BlockPlacementResult.Result.FINISHED);
					}

					var buildingInfo = new BuildingInfo(building, upgradeResources);

					synchronized (this.updatingBuildings)
					{
						this.updateProgress++;

						if (upgradeResources.size() > 0)
						{
							this.updatingBuildings.add(buildingInfo);
						}

					}

				}));
			}

		}

		if (this.nameFilterRequested > 0 && --this.nameFilterRequested == 0)
		{
			this.updateBuildingList();
		}

		this.updateBuilderList();
		this.updateUpgradeResources();
	}

	@Override
	public boolean click(double mx, double my)
	{
		var b = super.click(mx, my);

		if (b)
		{
			return b;
		}

		var buildingIndex = this.getHoveredRow(this.buildingList);

		if (buildingIndex > -1)
		{
			this.selectedBuildingIndex = buildingIndex;
			this.updateBuilderList();
			this.selectCurrentAssignedBuilder();
			return true;
		}

		var builderIndex = this.getHoveredRow(this.builderList);

		if (builderIndex > -1)
		{
			var building = this.filteredBuildings.get(this.selectedBuildingIndex);

			if (this.selectedBuilderIndex == builderIndex)
			{
				this.selectedBuilderIndex = -1;
				this.unassign(building);
			}
			else
			{
				this.selectedBuilderIndex = builderIndex;
				this.assign(building, this.filteredBuilders.get(builderIndex));
			}

			this.onBuildingCountsChanged();
			return true;
		}

		return false;
	}

	protected void selectCurrentAssignedBuilder()
	{
		if (this.selectedBuildingIndex == -1)
		{
			this.selectedBuilderIndex = -1;
			return;
		}

		var building = this.filteredBuildings.get(this.selectedBuildingIndex);
		var builder = this.assignments.get(building);
		this.selectedBuilderIndex = this.filteredBuilders.indexOf(builder);
	}

	protected int getHoveredRow(ScrollingList list)
	{
		var children = list.getContainer().getChildren();

		for (var i = 0; i < children.size(); i++)
		{
			var pane = children.get(i);

			if (pane instanceof Box && pane.wasCursorInPane())
			{
				return i;
			}

		}

		return -1;
	}

	@Override
	public boolean rightClick(double mx, double my)
	{
		var b = super.rightClick(mx, my);

		if (b)
		{
			return b;
		}

		var buildingIndex = this.getHoveredRow(this.buildingList);

		if (buildingIndex > -1)
		{
			var building = this.filteredBuildings.get(buildingIndex);

			if (building.dontUpgrade)
			{
				this.unmarkAsDontUpgrade(building);
			}
			else
			{
				this.markAsDontUpgrade(building);
			}

			this.selectedBuildingIndex = buildingIndex;
			this.onBuildingDontUpgradeChanged();
			return true;
		}

		return false;
	}

	@Override
	public void onButtonClicked(@NotNull Button button)
	{
		super.onButtonClicked(button);

		if (Objects.equals(button.getID(), BUTTON_ASSIGN_AUTO))
		{
			this.filteredBuildings.forEach(this::unassign);
			var groupsMap = this.filteredBuildings.stream().filter(building -> !building.dontUpgrade).collect(Collectors.groupingBy(building -> building.building.getBuildingLevel()));

			for (var buildingLevel = Constants.MAX_BUILDING_LEVEL; buildingLevel > -1; buildingLevel--)
			{
				var buildings = groupsMap.get(buildingLevel);

				if (buildings == null)
				{
					continue;
				}

				for (var building : buildings)
				{
					var builders = this.streamWorkableBuilders(building).collect(Collectors.toList());

					if (builders.size() == 0)
					{
						continue;
					}

					builders.sort((o1, o2) -> this.compareBuilderForAssign(building, o1, o2));
					var builder = builders.get(0);
					this.assign(building, builder);
				}

			}

			this.onBuildingCountsChanged();
			this.selectCurrentAssignedBuilder();
		}
		else if (Objects.equals(button.getID(), BUTTON_ASSGIN_CLEAR))
		{
			this.filteredBuildings.forEach(this::unassign);
			this.onBuildingCountsChanged();
			this.selectCurrentAssignedBuilder();
		}
		else if (Objects.equals(button.getID(), BUTTON_MARK_ALL))
		{
			this.filteredBuildings.forEach(this::markAsDontUpgrade);
			this.onBuildingDontUpgradeChanged();
		}
		else if (Objects.equals(button.getID(), BUTTON_MARK_CLAR))
		{
			this.filteredBuildings.forEach(this::unmarkAsDontUpgrade);
			this.onBuildingDontUpgradeChanged();
		}
		else if (Objects.equals(button.getID(), "upgrade"))
		{
			if (this.updating)
			{
				return;
			}

			for (var entry : this.assignments.entrySet())
			{
				Network.getNetwork().sendToServer(new BuildRequestMessage(entry.getKey().building, BuildRequestMessage.Mode.BUILD, entry.getValue().building.getPosition()));
			}

			var data = new BatchUpgradeData();

			for (var building : this.buildings.values())
			{
				if (building.dontUpgrade)
				{
					this.savedDontUpgrades.add(building.building.getID());
				}

			}

			this.savedDontUpgrades.forEach(data.getMarkAsDontUpgrades()::add);
			MineColoniesTweaks.network().sendToServer(new BatchUpgradeDataSaveMessage(this.colony, data));

			this.close();
		}
		else if (Objects.equals(button.getID(), WindowConstants.BUTTON_CANCEL))
		{
			this.close();
		}

	}

	protected int compareBuilderForAssign(BuildingInfo building, BuilderInfo o1, BuilderInfo o2)
	{
		if (o1.cachedAssignedCount != o2.cachedAssignedCount)
		{
			return Integer.compare(o1.cachedAssignedCount, o2.cachedAssignedCount);
		}

		return Double.compare(o1.getDistance(building), o2.getDistance(building));
	}

	protected void onBuildingDontUpgradeChanged()
	{
		this.updateBuilderList();
		this.selectCurrentAssignedBuilder();
		this.onBuildingCountsChanged();
	}

	protected Stream<BuilderInfo> streamWorkableBuilders(BuildingInfo building)
	{
		return this.builders.stream().filter(builder -> this.testWorkable(building, builder));
	}

	protected boolean testWorkable(BuildingInfo building, BuilderInfo builder)
	{
		var builderLevel = builder.building.getBuildingLevel();
		var buildingLevel = building.building.getBuildingLevel();

		if (building.building instanceof BuildingBuilder.View)
		{
			return builderLevel >= buildingLevel;
		}
		else
		{
			return builderLevel > buildingLevel;
		}

	}

	protected void onBuildingCountsChanged()
	{
		if (this.updating)
		{
			this.selectionText.setText(Component.translatable("minecolonies_tweaks.gui.updating"));
		}
		else
		{
			this.selectionText.setText(Component.translatable("minecolonies_tweaks.gui.assigned_counts", this.assignments.size(), this.buildings.values().stream().filter(this::testBuildingForCount).count()));
		}

	}

	protected boolean testBuildingForCount(BuildingInfo info)
	{
		return !info.dontUpgrade;
	}

	protected void updateBuildingList()
	{
		this.selectedBuildingIndex = -1;
		this.selectedBuilderIndex = -1;
		this.filteredBuildings.clear();

		var field = this.nameField.getText().toLowerCase(Locale.ENGLISH);
		this.buildings.values().stream().filter(this::testBuildingForList).filter(i -> this.filterBuilding(field, i)).forEach(this.filteredBuildings::add);
		this.filteredBuildings.sort(this::compareBuilding);

		this.buildingList.refreshElementPanes();

		this.updateBuilderList();
		this.updateUpgradeResources();
	}

	protected int compareBuilding(BuildingInfo building1, BuildingInfo building2)
	{
		var dontUpgrade1 = building1.dontUpgrade;
		var dontUpgrade2 = building2.dontUpgrade;

		if (dontUpgrade1 != dontUpgrade2)
		{
			return Boolean.compare(dontUpgrade1, dontUpgrade2);
		}

		var builder1 = this.assignments.get(building1);
		var builder2 = this.assignments.get(building2);
		var assigned1 = builder1 != null;
		var assigned2 = builder2 != null;

		if (assigned1 != assigned2)
		{
			return Boolean.compare(assigned2, assigned1);
		}
		else if (assigned1)
		{
			return builder1.nameLowerCase.compareTo(builder2.nameLowerCase);
		}

		return Integer.compare(building1.itemId, building2.itemId);
	}

	protected void updateBuildingRow(int index, Pane row)
	{
		var building = this.filteredBuildings.get(index);
		var builder = this.assignments.get(building);

		var buildingIcon = row.findPaneOfTypeByID(ICON_BUILDING, ItemIcon.class);
		buildingIcon.setItem(building.icon);

		var buildingLabel = row.findPaneOfTypeByID(TEXT_BUILDING_NAME, Text.class);
		buildingLabel.setText(Component.empty().append(building.name));
		buildingLabel.setColors(this.getBuildingLabelColor(building, index).getColor());

		var builderLabel = row.findPaneOfTypeByID(TEXT_BUILDER_NAME, Text.class);
		List<MutableComponent> builderTooltip = null;
		MutableComponent builderText = null;

		if (building.dontUpgrade)
		{
			builderText = Component.translatable("minecolonies_tweaks.gui.dont_upgrade").withStyle(ChatFormatting.GRAY);
		}
		else
		{
			builderText = Component.translatable("minecolonies_tweaks.gui.assigned_builder_name", builder != null ? (Component.translatable("minecolonies_tweaks.gui.builder_name_with_level", builder.name, builder.building.getBuildingLevel())) : Component.translatable("minecolonies_tweaks.gui.builder_no_assigned").withStyle(ChatFormatting.RED));
		}

		builderLabel.setText(builderText);

		if (builderLabel.getHoverPane() instanceof Tooltip tooltip)
		{
			tooltip.setText(builderTooltip);
		}

	}

	protected ChatFormatting getBuildingLabelColor(BuildingInfo building, int index)
	{
		if (this.selectedBuildingIndex == index)
		{
			return ChatFormatting.GOLD;
		}
		else if (building.dontUpgrade)
		{
			return ChatFormatting.GRAY;
		}
		else
		{
			return ChatFormatting.WHITE;
		}

	}

	protected boolean testBuildingForList(BuildingInfo building)
	{
		return true;
	}

	protected boolean filterBuilding(String filter, BuildingInfo building)
	{
		if (filter.isEmpty())
		{
			return true;
		}
		else if (building.idLowerCase.contains(filter))
		{
			return true;
		}
		else if (building.nameLowerCase.contains(filter))
		{
			return true;
		}

		var builder = this.assignments.get(building);

		if (builder != null && builder.nameLowerCase.contains(filter))
		{
			return true;
		}

		return false;
	}

	protected void updateBuilderList()
	{
		var buildingIndex = this.selectedBuildingIndex;

		if (this.lastBuildersBuildingIndex == buildingIndex)
		{
			return;
		}

		this.lastBuildersBuildingIndex = buildingIndex;
		this.filteredBuilders.clear();

		if (buildingIndex > -1)
		{
			var building = this.filteredBuildings.get(buildingIndex);
			this.builders.stream().filter(builder -> this.testWorkable(building, builder)).forEach(this.filteredBuilders::add);
			this.filteredBuilders.sort((o1, o2) -> this.compareBuilder(building, o1, o2));
		}

		this.builderList.refreshElementPanes();
	}

	protected int compareBuilder(BuildingInfo building, BuilderInfo builder1, BuilderInfo builder2)
	{
		var assigned = this.assignments.get(building);

		if (assigned == builder1)
		{
			return -1;
		}
		else if (assigned == builder2)
		{
			return 1;
		}

		return Double.compare(builder1.getDistance(building), builder2.getDistance(building));
	}

	protected void updateBuilderRow(int index, Pane row)
	{
		var builder = this.filteredBuilders.get(index);
		var building = this.lastBuildersBuildingIndex == -1 ? null : this.filteredBuildings.get(this.lastBuildersBuildingIndex);

		var builderLabel = row.findPaneOfTypeByID(TEXT_BUILDER_NAME, Text.class);
		builderLabel.setText(Component.translatable("minecolonies_tweaks.gui.builder_name_with_level", builder.name, builder.building.getBuildingLevel()));
		builderLabel.setColors(this.getBuilderLabelColor(index).getColor());

		var assignedCountLabel = row.findPaneOfTypeByID(TEXT_ASSIGNED_COUNT, Text.class);
		assignedCountLabel.setText(Component.translatable("minecolonies_tweaks.gui.assigned_count_with_value", builder.cachedAssignedCount));

		var distanceLabel = row.findPaneOfTypeByID(TEXT_DISTANCE_WITH_BUILDING, Text.class);
		distanceLabel.setText(Component.translatable("minecolonies_tweaks.gui.distance_with_building", (int) builder.getDistance(building)));
	}

	protected void updateUpgradeResources()
	{
		var buildingIndex = this.selectedBuildingIndex;

		if (this.lastResourcesBuildingIndex == buildingIndex)
		{
			return;
		}

		this.lastResourcesBuildingIndex = buildingIndex;
		this.upgradeResources.clear();

		if (buildingIndex > -1)
		{
			this.upgradeResources.addAll(this.filteredBuildings.get(buildingIndex).upgradeResources);
			this.upgradeResources.sort(this::compareResource);
		}

		this.upgradeResourceList.refreshElementPanes();

		for (var pane : this.window.getChildren())
		{
			if (pane instanceof AutomaticTooltip tooltip)
			{
				tooltip.setTextOld(Arrays.asList());
			}

		}

	}

	protected int compareResource(ItemStorage stack1, ItemStorage stack2)
	{
		var id1 = Item.getId(stack1.getItem());
		var id2 = Item.getId(stack2.getItem());
		return Integer.compare(id1, id2);
	}

	protected void updateUpgradeResourceRow(int index, Pane row)
	{
		var storage = this.upgradeResources.get(index);
		var stack = storage.getItemStack();
		stack.setCount(storage.getAmount());

		var icon = row.findPaneOfTypeByID(WindowConstants.RESOURCE_ICON, ItemIcon.class);
		icon.setItem(stack);

		var label = row.findPaneOfTypeByID(WindowConstants.RESOURCE_NAME, Text.class);
		label.setText(stack.getHoverName());
	}

	protected ChatFormatting getBuilderLabelColor(int index)
	{
		if (this.selectedBuilderIndex == index)
		{
			return ChatFormatting.GOLD;
		}
		else
		{
			return ChatFormatting.WHITE;
		}

	}

	@Override
	public boolean onUnhandledKeyTyped(int ch, int key)
	{
		if (key == GLFW.GLFW_KEY_ESCAPE)
		{
			this.selectedBuildingIndex = -1;
		}

		return super.onUnhandledKeyTyped(ch, key);
	}

	protected void assign(BuildingInfo building, BuilderInfo builder)
	{
		building.dontUpgrade = false;
		var prevBuilder = this.assignments.put(building, builder);

		if (prevBuilder != builder)
		{
			builder.cachedAssignedCount++;

			if (prevBuilder != null)
			{
				prevBuilder.cachedAssignedCount--;
			}

		}

	}

	protected void unassign(BuildingInfo building)
	{
		var builder = this.assignments.remove(building);

		if (builder != null)
		{
			builder.cachedAssignedCount--;
		}

	}

	protected void markAsDontUpgrade(BuildingInfo building)
	{
		building.dontUpgrade = true;
		this.unassign(building);
	}

	protected void unmarkAsDontUpgrade(BuildingInfo building)
	{
		building.dontUpgrade = false;
	}

	public static class BuildingInfo
	{
		public final IBuildingView building;
		public final Component name;
		public final String idLowerCase;
		public final String nameLowerCase;
		public final ItemStack icon;
		public final int itemId;

		public final List<ItemStorage> upgradeResources;

		public boolean dontUpgrade = false;

		public BuildingInfo(IBuildingView building, Map<ItemStorage, AtomicInteger> upgradeResources)
		{
			this.building = building;

			var buildingEntry = building.getBuildingType();
			this.name = BuildingUtils.getDisplayName(building);
			this.idLowerCase = buildingEntry.getRegistryName().toString().toLowerCase(Locale.ENGLISH);
			this.nameLowerCase = this.name.getString().toLowerCase(Locale.ENGLISH);
			this.icon = new ItemStack(buildingEntry.getBuildingBlock());
			this.itemId = Item.getId(this.icon.getItem());

			this.upgradeResources = upgradeResources.entrySet().stream().map(this::toItemStorage).toList();
		}

		private ItemStorage toItemStorage(Entry<ItemStorage, AtomicInteger> entry)
		{
			var stack = entry.getKey().copy();
			stack.setAmount(entry.getValue().get());
			return stack;
		}

	}

	public static class BuilderInfo
	{
		public final ICitizenDataView citizen;
		public final Component name;
		public final String nameLowerCase;
		public final IBuildingView building;

		public int cachedAssignedCount = 0;

		private final Object2DoubleMap<BuildingInfo> distances = new Object2DoubleOpenHashMap<>();

		public BuilderInfo(IColonyView colony, ICitizenDataView builder)
		{
			this.citizen = builder;
			this.name = Component.literal(builder.getName());
			this.nameLowerCase = builder.getName().toLowerCase(Locale.ENGLISH);
			this.building = colony.getBuilding(builder.getWorkBuilding());
		}

		public double getDistance(BuildingInfo building)
		{
			return this.distances.computeIfAbsent(building, (BuildingInfo b) ->
			{
				var pos1 = this.building.getPosition();
				var pos2 = b.building.getPosition();
				return Math.sqrt(pos1.distSqr(pos2));
			});
		}

	}

	public IColonyView getColony()
	{
		return this.colony;
	}

	public Map<BuildingInfo, BuilderInfo> getAssignments()
	{
		return new HashMap<>(this.assignments);
	}

}
