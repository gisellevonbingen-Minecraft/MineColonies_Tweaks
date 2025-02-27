package steve_gall.minecolonies_tweaks.core.common.init;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.minecolonies.core.items.ItemResourceScroll;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.item.ItemResourceScrollBook;

public class MCTweaksItems
{
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MineColoniesTweaks.MOD_ID);
	public static final Map<DyeColor, RegistryObject<ItemResourceScroll>> COLOR_RESOURCE_SCROLLS;
	public static final RegistryObject<ItemResourceScrollBook> RESOURCESCROLL_BOOK = REGISTER.register("resourcescroll_book", () -> new ItemResourceScrollBook(new Item.Properties(), 9 * 3));

	static
	{
		var colorResourceScrolls = new HashMap<DyeColor, RegistryObject<ItemResourceScroll>>();
		COLOR_RESOURCE_SCROLLS = Collections.unmodifiableMap(colorResourceScrolls);

		for (var color : DyeColor.values())
		{
			var obj = REGISTER.register("resourcescroll_" + color.getName().toLowerCase(), () -> new ItemResourceScroll(new Item.Properties()));
			colorResourceScrolls.put(color, obj);
		}

	}

	private MCTweaksItems()
	{

	}

}
