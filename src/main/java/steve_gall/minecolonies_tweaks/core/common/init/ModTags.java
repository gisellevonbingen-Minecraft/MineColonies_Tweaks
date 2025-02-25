package steve_gall.minecolonies_tweaks.core.common.init;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class ModTags
{
	public static class Items
	{
		public static final TagKey<Item> RESOURCESCOLLRS = create("resourcescrolls");

		public static TagKey<Item> create(String path)
		{
			return ItemTags.create(MineColoniesTweaks.rl(path));
		}

		private Items()
		{

		}

	}

	private ModTags()
	{

	}

}
