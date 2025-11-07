package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.controls.TextField;
import com.ldtteam.blockui.views.ScrollingList;
import com.minecolonies.api.util.constant.WindowConstants;
import com.minecolonies.core.client.gui.AbstractModuleWindow;
import com.minecolonies.core.datalistener.StudyItemListener;
import com.minecolonies.core.datalistener.StudyItemListener.StudyItem;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.building.module.StudyItemListModule;

public class StudyItemListModuleWindow extends AbstractModuleWindow<StudyItemListModule.View>
{
	public static final String OUTPUT_ICON = "outputIcon";
	public static final String OUTPUT_NAME = "outputName";
	public static final Component ON = Component.translatable(WindowConstants.ON);
	public static final Component OFF = Component.translatable(WindowConstants.OFF);

	protected final StudyItemListModule.View module;
	protected final ScrollingList resourceList;

	private final List<ItemCache> groupedItemList;
	private final Map<ItemStack, String> descriptionCache;
	private final Map<ItemStack, String> hoverNameCache;

	private String filter = "";
	private int tick = 0;
	private List<ItemCache> currentDisplayedList;

	public StudyItemListModuleWindow(StudyItemListModule.View module, ResourceLocation res)
	{
		super(module, res);

		this.module = module;
		this.resourceList = this.window.findPaneOfTypeByID(WindowConstants.LIST_RESOURCES, ScrollingList.class);

		this.groupedItemList = new ArrayList<>(StudyItemListener.getAllStudyItems().entrySet().stream().map(ItemCache::new).toList());
		this.descriptionCache = new HashMap<>();
		this.hoverNameCache = new HashMap<>();

		this.window.findPaneOfTypeByID(WindowConstants.INPUT_FILTER, TextField.class).setHandler(input ->
		{
			this.setFilter(input.getText());
		});
	}

	@Override
	public void onButtonClicked(@NotNull Button button)
	{
		super.onButtonClicked(button);

		var buttonId = button.getID();

		if (Objects.equals(buttonId, WindowConstants.BUTTON_SWITCH))
		{
			var row = this.resourceList.getListElementIndexByPane(button);
			var item = this.currentDisplayedList.get(row);
			this.toggleItems(Arrays.asList(item));
		}
		else if (Objects.equals(buttonId, WindowConstants.BUTTON_RESET_DEFAULT))
		{
			this.clearItems();
		}
		else if (Objects.equals(buttonId, "toggleInCurrent"))
		{
			this.toggleItems(this.currentDisplayedList);
		}
		else if (Objects.equals(buttonId, "resetInCurrent"))
		{
			this.removeItems(this.currentDisplayedList);
		}

	}

	public void addItems(Collection<ItemCache> items)
	{
		this.module.addIds(items.stream().map(item -> item.id).toList());
		this.resourceList.refreshElementPanes();
	}

	public void removeItems(Collection<ItemCache> items)
	{
		this.module.removeIds(items.stream().map(item -> item.id).toList());
		this.resourceList.refreshElementPanes();
	}

	public void toggleItems(Collection<ItemCache> items)
	{
		var toRemoves = items.stream().map(item -> item.id).filter(id -> this.module.containsId(id)).toList();
		var toAddes = items.stream().map(item -> item.id).filter(id -> !this.module.containsId(id)).toList();

		this.module.removeIds(toRemoves);
		this.module.addIds(toAddes);
		this.resourceList.refreshElementPanes();
	}

	public void clearItems()
	{
		this.module.clearIds();
		this.resourceList.refreshElementPanes();
	}

	public void setFilter(String newFilter)
	{
		if (!newFilter.equals(this.filter))
		{
			this.filter = newFilter;
			this.tick = 10;
		}

	}

	@Override
	public void onOpened()
	{
		super.onOpened();

		this.updateResources();
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.tick > 0 && --this.tick == 0)
		{
			this.updateResources();
		}

	}

	private boolean testFilter(ItemStack stack, String lowerCaseFilter)
	{
		return this.descriptionCache.computeIfAbsent(stack, s -> s.getDescriptionId().toLowerCase(Locale.US)).contains(lowerCaseFilter) || //
				this.hoverNameCache.computeIfAbsent(stack, s -> s.getHoverName().getString().toLowerCase(Locale.US)).contains(lowerCaseFilter);
	}

	private boolean testFilter(ItemCache item, String lowerCaseFilter)
	{
		return this.testFilter(item.icon, lowerCaseFilter);
	}

	protected void updateResources()
	{
		var lowerCase = this.filter.toLowerCase(Locale.US);
		Predicate<ItemCache> filterPredicate = this.filter.isEmpty() ? (item -> true) : (item -> this.testFilter(item, lowerCase));

		if (this.currentDisplayedList != null)
		{
			this.currentDisplayedList.clear();
			this.updateResourceList();
		}

		this.currentDisplayedList = new ArrayList<>();
		this.groupedItemList.stream().filter(filterPredicate).forEach(this.currentDisplayedList::add);
		this.currentDisplayedList.sort(this::compareResources);

		this.updateResourceList();
	}

	protected int compareResources(ItemCache item1, ItemCache item2)
	{
		var isInverted = this.module.isInverted();
		var contains1 = this.module.containsId(item1.id);
		var contains2 = this.module.containsId(item2.id);

		if (isInverted)
		{
			return Boolean.compare(contains1, contains2);
		}
		else
		{
			return Boolean.compare(contains2, contains1);
		}

	}

	/**
	 * Updates the resource list in the GUI with the info we need.
	 */
	private void updateResourceList()
	{
		this.resourceList.enable();
		this.resourceList.show();
		this.resourceList.setDataProvider(new ScrollingList.DataProvider()
		{
			@Override
			public int getElementCount()
			{
				return currentDisplayedList.size();
			}

			@Override
			public void updateElement(int index, Pane rowPane)
			{
				var item = currentDisplayedList.get(index);

				var outputIcon = rowPane.findPaneOfTypeByID(OUTPUT_ICON, ItemIcon.class);
				outputIcon.setItem(item.icon);

				var outputLabel = rowPane.findPaneOfTypeByID(OUTPUT_NAME, Text.class);
				outputLabel.setText(item.icon.getHoverName());
				outputLabel.setColors(0x000000);

				var breakChanceLabel = rowPane.findPaneOfTypeByID("breakChance", Text.class);
				breakChanceLabel.setText(item.tooltip.get(0));
				breakChanceLabel.setColors(0x000000);

				var contains = module.containsId(item.id);
				var isInverted = module.isInverted();
				var switchButton = rowPane.findPaneOfTypeByID(WindowConstants.BUTTON_SWITCH, Button.class);
				var on = (isInverted && !contains) || (!isInverted && contains);
				switchButton.setText(on ? ON : OFF);
			}
		});
	}

	public class ItemCache
	{
		public final ResourceLocation id;
		public final StudyItem item;
		public final ItemStack icon;
		public final List<MutableComponent> tooltip;

		public ItemCache(Entry<ResourceLocation, StudyItem> entry)
		{
			this.id = entry.getKey();
			this.item = entry.getValue();
			this.icon = new ItemStack(this.item.item());
			this.tooltip = Arrays.asList(//
					Component.translatable("minecolonies_tweaks.gui.break_chance", this.item.breakChance())//
			);
		}

	}

}
