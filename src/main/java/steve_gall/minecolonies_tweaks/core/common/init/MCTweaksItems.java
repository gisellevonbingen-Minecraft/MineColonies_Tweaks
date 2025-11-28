package steve_gall.minecolonies_tweaks.core.common.init;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.minecolonies.core.items.ItemResourceScroll;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.item.ItemCopyScroll;
import steve_gall.minecolonies_tweaks.core.common.item.ItemInventoryScroll;
import steve_gall.minecolonies_tweaks.core.common.item.ItemResourceScrollBook;
import steve_gall.minecolonies_tweaks.core.common.item.ItemUniversityScroll;

public class MCTweaksItems
{
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, MineColoniesTweaks.MOD_ID);
	public static final Map<DyeColor, DeferredHolder<Item, ItemResourceScroll>> COLOR_RESOURCE_SCROLLS;
	public static final DeferredHolder<Item, ItemResourceScrollBook> RESOURCESCROLL_BOOK = REGISTER.register("resourcescroll_book", () -> new ItemResourceScrollBook(new Item.Properties(), 9 * 3));
	public static final DeferredHolder<Item, ItemInventoryScroll> INVENTORYSCROLL = REGISTER.register("inventoryscroll", () -> new ItemInventoryScroll(new Item.Properties()));
	public static final Map<DyeColor, DeferredHolder<Item, ItemInventoryScroll>> COLOR_INVENTORY_SCROLLS;
	public static final DeferredHolder<Item, ItemUniversityScroll> UNIVERSITYSCROLL = REGISTER.register("universityscroll", () -> new ItemUniversityScroll(new Item.Properties()));
	public static final DeferredHolder<Item, ItemCopyScroll> COPYSCROLL = REGISTER.register("copyscroll", () -> new ItemCopyScroll(new Item.Properties()));

	static
	{
		var colorResourceScrolls = new HashMap<DyeColor, DeferredHolder<Item, ItemResourceScroll>>();
		COLOR_RESOURCE_SCROLLS = Collections.unmodifiableMap(colorResourceScrolls);
		var colorWarehouseScrolls = new HashMap<DyeColor, DeferredHolder<Item, ItemInventoryScroll>>();
		COLOR_INVENTORY_SCROLLS = Collections.unmodifiableMap(colorWarehouseScrolls);

		for (var color : DyeColor.values())
		{
			var resourceScroll = REGISTER.register("resourcescroll_" + color.getName().toLowerCase(), () -> new ItemResourceScroll(new Item.Properties()));
			colorResourceScrolls.put(color, resourceScroll);

			var warehouseScroll = REGISTER.register("inventoryscroll_" + color.getName().toLowerCase(), () -> new ItemInventoryScroll(new Item.Properties()));
			colorWarehouseScrolls.put(color, warehouseScroll);
		}

	}

	private MCTweaksItems()
	{

	}

}
