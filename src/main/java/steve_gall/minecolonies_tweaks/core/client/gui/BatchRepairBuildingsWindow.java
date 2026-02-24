package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.ldtteam.structurize.api.RotationMirror;
import com.ldtteam.structurize.placement.AbstractBlueprintIterator;
import com.ldtteam.structurize.placement.BlockPlacementResult;
import com.ldtteam.structurize.placement.StructurePhasePlacementResult;
import com.ldtteam.structurize.placement.StructurePlacer;
import com.ldtteam.structurize.storage.ClientFutureProcessor;
import com.ldtteam.structurize.storage.StructurePacks;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.LoadOnlyStructureHandler;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.WindowConstants;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.network.messages.server.colony.building.BuildRequestMessage;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.neoforged.neoforge.network.PacketDistributor;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingUtils;
import steve_gall.minecolonies_tweaks.core.common.colony.BatchRepairData;
import steve_gall.minecolonies_tweaks.core.common.colony.BuildingCost;
import steve_gall.minecolonies_tweaks.core.common.network.message.BatchRepairDataLoadMessage;
import steve_gall.minecolonies_tweaks.core.common.network.message.BatchRepairDataSaveMessage;

public class BatchRepairBuildingsWindow extends AbstractWindowSkeleton
{
	public static final Component O = Component.literal("O");
	public static final Component X = Component.literal("X");
	public static final Component RESOURCES_CHANGED_SUFFIX = Component.literal("*").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);

	public static final String LIST_BUILDINGS = "buildings";
	public static final String LIST_BUILDERS = "builders";
	public static final String LIST_REPAIR_RESOURCES = "repairResources";
	public static final String ICON_BUILDING = "buildingIcon";
	public static final String TEXT_BUILDING_NAME = "buildingName";
	public static final String TEXT_BUILDER_NAME = "builderName";
	public static final String TEXT_ASSIGNED_COUNT = "assignedCount";
	public static final String TEXT_DISTANCE_WITH_BUILDING = "distanceWithBuilding";
	public static final String BUTTON_EXCEPT_OPENABLES_ONLY_CHANGED = "exceptOpenablesOnlyChanged";
	public static final String BUTTON_ASSIGN_AUTO = "assignAllAutomatically";
	public static final String BUTTON_ASSGIN_CLEAR = "clearAssignments";
	public static final String BUTTON_MARK_ALL = "markAllDontRepair";
	public static final String BUTTON_MARK_CLAR = "clearDontRepair";
	public static final String TEXT_SELECTION = "selectionText";

	private final IColonyView colony;
	private final BOWindow parent;

	private final TextField nameField;
	private final Button exceptButton;
	private final ScrollingList buildingList;
	private final ScrollingList builderList;
	private final ScrollingList repairResourceList;
	private final Text selectionText;

	private final Map<BlockPos, BuildingCost> savedCosts;
	private final Set<BlockPos> savedDontRepairs;

	private final List<BuildingInfo> updatingBuildings;
	private final Map<BlockPos, BuildingInfo> buildings;
	private final List<BuildingInfo> filteredBuildings;
	private final List<BuilderInfo> builders;
	private final List<BuilderInfo> filteredBuilders;
	private final List<ItemStorage> repairResources;
	private final Map<BuildingInfo, BuilderInfo> assignments;

	private boolean requested = false;
	private boolean updating = false;
	private int updateProgress = 0;
	private int updateCount = 0;
	private boolean exceptOpenablesOnlyChanged = false;
	private int selectedBuildingIndex = -1;
	private int selectedBuilderIndex = -1;
	private int lastBuildersBuildingIndex = -1;
	private int lastResourcesBuildingIndex = -1;
	private int nameFilterRequested = 0;

	public BatchRepairBuildingsWindow(@Nullable BOWindow parent, IColonyView colony)
	{
		super(null, MineColoniesTweaks.rl("gui/batch_repair_buildings_window.xml"));
		this.colony = colony;
		this.parent = parent;

		this.nameField = this.window.findPaneOfTypeByID(WindowConstants.INPUT_FILTER, TextField.class);
		this.exceptButton = this.window.findPaneOfTypeByID(BUTTON_EXCEPT_OPENABLES_ONLY_CHANGED, Button.class);
		this.buildingList = this.window.findPaneOfTypeByID(LIST_BUILDINGS, ScrollingList.class);
		this.builderList = this.window.findPaneOfTypeByID(LIST_BUILDERS, ScrollingList.class);
		this.repairResourceList = this.window.findPaneOfTypeByID(LIST_REPAIR_RESOURCES, ScrollingList.class);
		this.selectionText = this.window.findPaneOfTypeByID(TEXT_SELECTION, Text.class);

		this.savedCosts = new HashMap<>();
		this.savedDontRepairs = new HashSet<>();

		this.updatingBuildings = new ArrayList<>();
		this.buildings = new HashMap<>();
		this.filteredBuildings = new ArrayList<>();
		this.builders = new ArrayList<>();
		this.filteredBuilders = new ArrayList<>();
		this.repairResources = new ArrayList<>();
		this.assignments = new HashMap<>();

		this.nameField.setHandler(this::onFieldInput);
		this.buildingList.setDataProvider(this.filteredBuildings::size, this::updateBuildingRow);
		this.builderList.setDataProvider(this.filteredBuilders::size, this::updateBuilderRow);
		this.repairResourceList.setDataProvider(this.repairResources::size, this::updateRepairResourceRow);
	}

	public void setSavedBuildings(BatchRepairData data)
	{
		this.savedCosts.clear();
		this.savedDontRepairs.clear();

		for (var building : data.getCosts())
		{
			this.savedCosts.put(building.id(), building);
		}

		for (var building : data.getMarkAsDontRepairs())
		{
			this.savedDontRepairs.add(building);
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

		if (this.savedDontRepairs.contains(id))
		{
			this.savedDontRepairs.remove(id);
			this.markAsDontRepair(building);
		}

		var buildingCosts = this.savedCosts.get(id);

		if (buildingCosts != null)
		{
			var prevCosts = buildingCosts.costs();
			var nextCosts = building.repairResources;
			building.repairResourcesChanged = !this.equalsCosts(prevCosts, nextCosts);
		}

	}

	private boolean equalsCosts(List<ItemStorage> prev, List<ItemStorage> next)
	{
		var size = next.size();

		if (prev.size() != size)
		{
			return false;
		}

		for (var i = 0; i < size; i++)
		{
			var prevStorage = prev.get(i);
			var nextStorage = next.get(i);

			if (prevStorage.getAmount() != nextStorage.getAmount())
			{
				return false;
			}
			else if (!prevStorage.equals(nextStorage))
			{
				return false;
			}

		}

		return true;
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
		this.onExceptOpenablesOnlyChangedChanged();

		PacketDistributor.sendToServer(new BatchRepairDataLoadMessage(this.colony));
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

			for (var building : this.colony.getClientBuildingManager().getBuildings().values())
			{
				if (building.hasWorkOrder() || !BuildingUtils.isUnlocked(this.colony, building.getBuildingType(), building.getBuildingLevel()))
				{
					continue;
				}

				var buildingName = building.getStructurePath().replace(".blueprint", "");

				if (buildingName.isEmpty())
				{
					continue;
				}

				this.updateCount++;
				buildingName = buildingName.substring(0, buildingName.length() - 1) + building.getBuildingLevel() + ".blueprint";
				ClientFutureProcessor.queueBlueprint(new ClientFutureProcessor.BlueprintProcessingData(StructurePacks.getBlueprintFuture(building.getStructurePack(), buildingName, level.registryAccess()), blueprint ->
				{
					var repairResources = new HashMap<ItemStorage, AtomicInteger>();

					if (blueprint != null)
					{
						blueprint.setRotationMirror(building.getRotationMirror(), level);
						var placer = new StructurePlacer(new LoadOnlyStructureHandler(level, building.getPosition(), blueprint, RotationMirror.NONE));
						StructurePhasePlacementResult result;
						var progressPos = AbstractBlueprintIterator.NULL_POS;

						do
						{
							result = placer.executeStructureStep(level, null, progressPos, StructurePlacer.Operation.GET_RES_REQUIREMENTS, () -> placer.getIterator().increment((info, pos, handler) -> false), true);
							progressPos = result.getIteratorPos();

							for (var stack : result.getBlockResult().getRequiredItems())
							{
								var existing = repairResources.computeIfAbsent(new ItemStorage(stack.copyWithCount(1)), s -> new AtomicInteger());
								existing.addAndGet(stack.getCount());
							}

						}
						while (result != null && result.getBlockResult().getResult() != BlockPlacementResult.Result.FINISHED);
					}

					var buildingInfo = new BuildingInfo(building, repairResources);

					synchronized (this.updatingBuildings)
					{
						this.updateProgress++;

						if (repairResources.size() > 0)
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
		this.updateRepairResources();
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

			if (building.dontRepair)
			{
				this.unmarkAsDontRepair(building);
			}
			else
			{
				this.markAsDontRepair(building);
			}

			this.selectedBuildingIndex = buildingIndex;
			this.onBuildingDontRepairChanged();
			return true;
		}

		return false;
	}

	@Override
	public void onButtonClicked(@NotNull Button button)
	{
		super.onButtonClicked(button);

		if (Objects.equals(button.getID(), BUTTON_EXCEPT_OPENABLES_ONLY_CHANGED))
		{
			this.exceptOpenablesOnlyChanged ^= true;
			this.onExceptOpenablesOnlyChangedChanged();
		}
		else if (Objects.equals(button.getID(), BUTTON_ASSIGN_AUTO))
		{
			this.filteredBuildings.forEach(this::unassign);
			var groupsMap = this.filteredBuildings.stream().filter(building -> !building.dontRepair).collect(Collectors.groupingBy(building -> building.building.getBuildingLevel()));

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
			this.filteredBuildings.forEach(this::markAsDontRepair);
			this.onBuildingDontRepairChanged();
		}
		else if (Objects.equals(button.getID(), BUTTON_MARK_CLAR))
		{
			this.filteredBuildings.forEach(this::unmarkAsDontRepair);
			this.onBuildingDontRepairChanged();
		}
		else if (Objects.equals(button.getID(), WindowConstants.BUTTON_REPAIR))
		{
			if (this.updating)
			{
				return;
			}

			for (var entry : this.assignments.entrySet())
			{
				new BuildRequestMessage(entry.getKey().building, BuildRequestMessage.Mode.REPAIR, entry.getValue().building.getPosition()).sendToServer();
			}

			var data = new BatchRepairData();

			for (var building : this.buildings.values())
			{
				data.getCosts().add(building.toCost());

				if (building.dontRepair)
				{
					this.savedDontRepairs.add(building.building.getID());
				}

			}

			this.savedDontRepairs.forEach(data.getMarkAsDontRepairs()::add);
			PacketDistributor.sendToServer(new BatchRepairDataSaveMessage(this.colony, data));

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

	protected void onExceptOpenablesOnlyChangedChanged()
	{
		var excpet = this.exceptOpenablesOnlyChanged;
		this.exceptButton.setText(excpet ? O : X);
		this.exceptButton.setColors((excpet ? ChatFormatting.BLACK : ChatFormatting.RED).getColor());

		this.buildings.values().stream().filter(building -> !this.testBuildingForList(building)).forEach(this::unassign);
		this.updateBuildingList();
		this.onBuildingCountsChanged();
	}

	protected void onBuildingDontRepairChanged()
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
		return builder.building.getBuildingLevel() >= building.building.getBuildingLevel();
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
		if (this.exceptOpenablesOnlyChanged && info.openableOnlyChanged)
		{
			return false;
		}

		return !info.dontRepair;
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
		this.updateRepairResources();
	}

	protected int compareBuilding(BuildingInfo building1, BuildingInfo building2)
	{
		var dontRepair1 = building1.dontRepair && !building1.repairResourcesChanged;
		var dontRepair2 = building2.dontRepair && !building2.repairResourcesChanged;

		if (dontRepair1 != dontRepair2)
		{
			return Boolean.compare(dontRepair1, dontRepair2);
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
		List<MutableComponent> buildingTooltip = null;
		MutableComponent buildingText = Component.empty().append(building.name);

		if (building.repairResourcesChanged)
		{
			buildingText = buildingText.append(RESOURCES_CHANGED_SUFFIX);
			buildingTooltip = Collections.singletonList(Component.translatable("minecolonies_tweaks.gui.resources_changed"));
		}

		buildingLabel.setText(buildingText);
		buildingLabel.setColors(this.getBuildingLabelColor(building, index).getColor());

		if (buildingLabel.getHoverPane() instanceof Tooltip tooltip)
		{
			tooltip.setText(buildingTooltip);
		}

		var builderLabel = row.findPaneOfTypeByID(TEXT_BUILDER_NAME, Text.class);
		List<MutableComponent> builderTooltip = null;
		MutableComponent builderText = null;

		if (building.dontRepair)
		{
			builderText = Component.translatable("minecolonies_tweaks.gui.dont_repair").withStyle(ChatFormatting.GRAY);
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
		else if (building.dontRepair)
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
		if (this.exceptOpenablesOnlyChanged && building.openableOnlyChanged)
		{
			return false;
		}

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

	protected void updateRepairResources()
	{
		var buildingIndex = this.selectedBuildingIndex;

		if (this.lastResourcesBuildingIndex == buildingIndex)
		{
			return;
		}

		this.lastResourcesBuildingIndex = buildingIndex;
		this.repairResources.clear();

		if (buildingIndex > -1)
		{
			this.repairResources.addAll(this.filteredBuildings.get(buildingIndex).repairResources);
			this.repairResources.sort(this::compareResource);
		}

		this.repairResourceList.refreshElementPanes();

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

	protected void updateRepairResourceRow(int index, Pane row)
	{
		var storage = this.repairResources.get(index);
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
		building.dontRepair = false;
		building.repairResourcesChanged = false;
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

	protected void markAsDontRepair(BuildingInfo building)
	{
		building.dontRepair = true;
		building.repairResourcesChanged = false;
		this.unassign(building);
	}

	protected void unmarkAsDontRepair(BuildingInfo building)
	{
		building.dontRepair = false;
	}

	public static class BuildingInfo
	{
		public final IBuildingView building;
		public final Component name;
		public final String idLowerCase;
		public final String nameLowerCase;
		public final ItemStack icon;
		public final int itemId;

		public final List<ItemStorage> repairResources;
		public final boolean openableOnlyChanged;

		public boolean dontRepair = false;
		public boolean repairResourcesChanged = false;

		public BuildingInfo(IBuildingView building, Map<ItemStorage, AtomicInteger> repairResources)
		{
			this.building = building;

			var buildingEntry = building.getBuildingType();
			this.name = BuildingUtils.getDisplayName(building);
			this.idLowerCase = buildingEntry.getRegistryName().toString().toLowerCase(Locale.ENGLISH);
			this.nameLowerCase = this.name.getString().toLowerCase(Locale.ENGLISH);
			this.icon = new ItemStack(buildingEntry.getBuildingBlock());
			this.itemId = Item.getId(this.icon.getItem());

			this.repairResources = repairResources.entrySet().stream().map(this::toItemStorage).toList();
			this.openableOnlyChanged = repairResources.keySet().stream().map(ItemStorage::getItem).allMatch(this::testExceptable);
		}

		public BuildingCost toCost()
		{
			return new BuildingCost(this.building.getID(), this.repairResources);
		}

		private ItemStorage toItemStorage(Entry<ItemStorage, AtomicInteger> entry)
		{
			var stack = entry.getKey().copy();
			stack.setAmount(entry.getValue().get());
			return stack;
		}

		private boolean testExceptable(Item item)
		{
			if (item instanceof BlockItem blockItem)
			{
				var block = blockItem.getBlock();

				if (block instanceof DoorBlock || block instanceof TrapDoorBlock)
				{
					return true;
				}

			}

			return false;
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
			this.building = colony.getClientBuildingManager().getBuilding(builder.getWorkBuilding());
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
