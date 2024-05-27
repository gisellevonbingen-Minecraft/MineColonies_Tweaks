package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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
import com.ldtteam.blockui.controls.Tooltip.AutomaticTooltip;
import com.ldtteam.blockui.views.BOWindow;
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
import com.minecolonies.core.network.messages.server.colony.building.BuildRequestMessage;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.TrapDoorBlock;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

@SuppressWarnings("removal")
public class BatchRepairBuildingsWindow extends AbstractWindowSkeleton
{
	public static final Component O = Component.literal("O");
	public static final Component X = Component.literal("X");

	public static final String LIST_BUILDINGS = "buildings";
	public static final String LIST_BUILDERS = "builders";
	public static final String LIST_REPAIR_RESOURCES = "repairResources";
	public static final String ICON_BUILDING = "buildingIcon";
	public static final String TEXT_BUILDING_NAME = "buildingName";
	public static final String TEXT_BUILDER_NAME = "builderName";
	public static final String TEXT_ASSIGNED_COUNT = "assignedCount";
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

	private final List<BuildingInfo> buildings;
	private final List<BuildingInfo> filteredBuildings;
	private final List<BuilderInfo> builders;
	private final List<BuilderInfo> filteredBuilders;
	private final List<ItemStack> repairResources;
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

	public BatchRepairBuildingsWindow(IColonyView colony, @Nullable BOWindow parent)
	{
		super(MineColoniesTweaks.rl("gui/batch_repair_buildings_window.xml").toString(), null);
		this.colony = colony;
		this.parent = parent;

		this.nameField = this.window.findPaneOfTypeByID(WindowConstants.INPUT_FILTER, TextField.class);
		this.exceptButton = this.window.findPaneOfTypeByID(BUTTON_EXCEPT_OPENABLES_ONLY_CHANGED, Button.class);
		this.buildingList = this.window.findPaneOfTypeByID(LIST_BUILDINGS, ScrollingList.class);
		this.builderList = this.window.findPaneOfTypeByID(LIST_BUILDERS, ScrollingList.class);
		this.repairResourceList = this.window.findPaneOfTypeByID(LIST_REPAIR_RESOURCES, ScrollingList.class);
		this.selectionText = this.window.findPaneOfTypeByID(TEXT_SELECTION, Text.class);

		this.buildings = new ArrayList<>();
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
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.updating)
		{
			synchronized (this.buildings)
			{
				if (this.updateProgress >= this.updateCount)
				{
					this.updating = false;
				}

				this.nameFilterRequested = 0;
				this.updateBuildingList();
			}

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
				if (building.isRepairing())
				{
					continue;
				}

				var buildingStyle = building.getStructurePack();
				var buildingName = building.getStructurePath().replace(".blueprint", "");

				this.updateCount++;

				if (buildingName.isEmpty())
				{
					return;
				}

				buildingName = buildingName.substring(0, buildingName.length() - 1) + building.getBuildingLevel() + ".blueprint";
				ClientFutureProcessor.queueBlueprint(new ClientFutureProcessor.BlueprintProcessingData(StructurePacks.getBlueprintFuture(buildingStyle, buildingName), blueprint ->
				{
					var repairResources = new HashMap<ItemStorage, AtomicInteger>();

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
								var existing = repairResources.computeIfAbsent(new ItemStorage(stack), s -> new AtomicInteger());
								existing.addAndGet(stack.getCount());
							}

						}
						while (result != null && result.getBlockResult().getResult() != BlockPlacementResult.Result.FINISHED);
					}

					var buildingInfo = new BuildingInfo(building, repairResources);

					synchronized (this.buildings)
					{
						this.updateProgress++;

						if (repairResources.size() > 0)
						{
							this.buildings.add(buildingInfo);
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
			if (children.get(i).wasCursorInPane())
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

				var builders = this.streamWorkableBuilders(buildingLevel).collect(Collectors.toList());

				if (builders.size() == 0)
				{
					continue;
				}

				for (var building : buildings)
				{
					builders.sort((o1, o2) -> Integer.compare(o1.cachedAssignedCount, o2.cachedAssignedCount));
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
			for (var entry : this.assignments.entrySet())
			{
				Network.getNetwork().sendToServer(new BuildRequestMessage(entry.getKey().building, BuildRequestMessage.Mode.REPAIR, entry.getValue().building.getPosition()));
			}

			this.close();
		}
		else if (Objects.equals(button.getID(), WindowConstants.BUTTON_CANCEL))
		{
			this.close();
		}

	}

	protected void onExceptOpenablesOnlyChangedChanged()
	{
		var excpet = this.exceptOpenablesOnlyChanged;
		this.exceptButton.setText(excpet ? O : X);
		this.exceptButton.setColors((excpet ? ChatFormatting.BLACK : ChatFormatting.RED).getColor());

		this.buildings.stream().filter(building -> !this.testBuildingForList(building)).forEach(this::unassign);
		this.updateBuildingList();
		this.onBuildingCountsChanged();
	}

	protected void onBuildingDontRepairChanged()
	{
		this.updateBuilderList();
		this.selectCurrentAssignedBuilder();
		this.onBuildingCountsChanged();
	}

	protected Stream<BuilderInfo> streamWorkableBuilders(int buildingLevel)
	{
		return this.builders.stream().filter(builder -> this.testWorkable(buildingLevel, builder));
	}

	protected boolean testWorkable(int buildingLevel, BuilderInfo builder)
	{
		return builder.building.getBuildingLevel() >= buildingLevel;
	}

	protected void onBuildingCountsChanged()
	{
		if (this.updating)
		{
			this.selectionText.setText(Component.translatable("minecolonies_tweaks.gui.updating"));
		}
		else
		{
			this.selectionText.setText(Component.translatable("minecolonies_tweaks.gui.assigned_counts", this.assignments.size(), this.buildings.stream().filter(this::testBuildingForCount).count()));
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

		synchronized (this.buildings)
		{
			var field = this.nameField.getText().toLowerCase(Locale.ENGLISH);
			this.buildings.stream().filter(this::testBuildingForList).filter(i -> this.filterBuilding(field, i)).forEach(this.filteredBuildings::add);
			this.filteredBuildings.sort(this::compareBuilding);
		}

		this.buildingList.refreshElementPanes();

		this.updateBuilderList();
		this.updateRepairResources();
	}

	protected int compareBuilding(BuildingInfo building1, BuildingInfo building2)
	{
		var dontRepair1 = building1.dontRepair;
		var dontRepair2 = building2.dontRepair;

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
		buildingLabel.setText(building.name);
		buildingLabel.setColors(this.getBuildingLabelColor(building, index).getColor());

		var builderLabel = row.findPaneOfTypeByID(TEXT_BUILDER_NAME, Text.class);

		if (building.dontRepair)
		{
			builderLabel.setText(Component.translatable("minecolonies_tweaks.gui.dont_repair").withStyle(ChatFormatting.GRAY));
		}
		else
		{
			builderLabel.setText(Component.translatable("minecolonies_tweaks.gui.assigned_builder_name", builder != null ? (Component.translatable("minecolonies_tweaks.gui.builder_name_with_level", builder.name, builder.building.getBuildingLevel())) : Component.translatable("minecolonies_tweaks.gui.builder_no_assigned").withStyle(ChatFormatting.RED)));
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
			var buildingLevel = building.building.getBuildingLevel();

			this.builders.stream().filter(builder -> this.testWorkable(buildingLevel, builder)).forEach(this.filteredBuilders::add);
			this.filteredBuilders.sort(this::compareBuilder);
		}

		this.builderList.refreshElementPanes();
	}

	protected int compareBuilder(BuilderInfo builder1, BuilderInfo builder2)
	{
		if (this.selectedBuildingIndex > -1)
		{
			var building = this.filteredBuildings.get(this.selectedBuildingIndex);
			var assigned = this.assignments.get(building);

			if (assigned == builder1)
			{
				return -1;
			}
			else if (assigned == builder2)
			{
				return 1;
			}

		}

		var level1 = builder1.building.getBuildingLevel();
		var level2 = builder2.building.getBuildingLevel();

		if (level1 != level2)
		{
			return Integer.compare(level2, level1);
		}

		return builder1.nameLowerCase.compareTo(builder2.nameLowerCase);
	}

	protected void updateBuilderRow(int index, Pane row)
	{
		var builder = this.filteredBuilders.get(index);

		var builderLabel = row.findPaneOfTypeByID(TEXT_BUILDER_NAME, Text.class);
		builderLabel.setText(Component.translatable("minecolonies_tweaks.gui.builder_name_with_level", builder.name, builder.building.getBuildingLevel()));
		builderLabel.setColors(this.getBuilderLabelColor(index).getColor());

		var assignedCountLabel = row.findPaneOfTypeByID(TEXT_ASSIGNED_COUNT, Text.class);
		assignedCountLabel.setText(Component.translatable("minecolonies_tweaks.gui.assigned_count_with_value", builder.cachedAssignedCount));
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

	protected int compareResource(ItemStack stack1, ItemStack stack2)
	{
		var id1 = Item.getId(stack1.getItem());
		var id2 = Item.getId(stack2.getItem());
		return Integer.compare(id1, id2);
	}

	protected void updateRepairResourceRow(int index, Pane row)
	{
		var stack = this.repairResources.get(index);

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

		public final List<ItemStack> repairResources;
		public final boolean openableOnlyChanged;

		public boolean dontRepair = false;

		public BuildingInfo(IBuildingView building, Map<ItemStorage, AtomicInteger> repairResources)
		{
			this.building = building;

			var buildingEntry = building.getBuildingType();
			var buildingId = buildingEntry.getRegistryName();
			var customName = building.getCustomName();
			var nameBase = customName.isEmpty() ? Component.translatable("com." + buildingId.getNamespace() + ".building." + buildingId.getPath()) : Component.literal(customName);
			this.name = nameBase.append(" ").append(String.valueOf(building.getBuildingLevel()));
			this.idLowerCase = buildingId.toString().toLowerCase(Locale.ENGLISH);
			this.nameLowerCase = this.name.getString().toLowerCase(Locale.ENGLISH);
			this.icon = new ItemStack(buildingEntry.getBuildingBlock());
			this.itemId = Item.getId(this.icon.getItem());

			this.repairResources = repairResources.entrySet().stream().map(this::toItemStack).toList();
			this.openableOnlyChanged = repairResources.keySet().stream().map(ItemStorage::getItem).allMatch(this::testExceptable);
		}

		private ItemStack toItemStack(Entry<ItemStorage, AtomicInteger> entry)
		{
			var stack = entry.getKey().getItemStack().copy();
			stack.setCount(entry.getValue().get());
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

		public BuilderInfo(IColonyView colony, ICitizenDataView builder)
		{
			this.citizen = builder;
			this.name = Component.literal(builder.getName());
			this.nameLowerCase = builder.getName().toLowerCase(Locale.ENGLISH);
			this.building = colony.getBuilding(builder.getWorkBuilding());
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
