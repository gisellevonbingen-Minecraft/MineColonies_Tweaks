package steve_gall.minecolonies_tweaks.core.common.init;

import java.util.function.Predicate;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class MCTweaksTags
{
	public static class Items
	{
		public static final TagKey<Item> FINE_FOOD = create("fine_food");
		public static final TagKey<Item> DECENT_FOOD = create("decent_food");
		public static final TagKey<Item> GREAT_FOOD = create("great_food");
		public static final TagKey<Item> RESOURCESCOLLRS = create("resourcescrolls");
		public static final TagKey<Item> RESOURCESCROLLBOOK_ELEMENT = create("resourcescrollbook_element");

		public static int getFoodTier(Predicate<TagKey<Item>> test)
		{
			if (test.test(GREAT_FOOD))
			{
				return 3;
			}
			else if (test.test(DECENT_FOOD))
			{
				return 2;
			}
			else if (test.test(FINE_FOOD))
			{
				return 1;
			}
			else
			{
				return 0;
			}

		}

		public static TagKey<Item> create(String name)
		{
			return ItemTags.create(MineColoniesTweaks.rl(name));
		}

		private Items()
		{

		}

	}

	private MCTweaksTags()
	{

	}

}
