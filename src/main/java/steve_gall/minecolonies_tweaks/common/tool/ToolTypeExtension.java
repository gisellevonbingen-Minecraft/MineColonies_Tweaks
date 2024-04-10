package steve_gall.minecolonies_tweaks.common.tool;

import java.util.HashMap;
import java.util.Map;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.IToolType;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.common.MineColoniesTweaks;

public class ToolTypeExtension
{
	private static Map<IToolType, ToolTypeExtension> MAP = new HashMap<>();

	public static ToolTypeExtension from(IToolType toolType)
	{
		return MAP.computeIfAbsent(toolType, ToolTypeExtension::new);
	}

	private final IToolType toolType;

	private TagKey<Item> itemTag;
	private final Int2ObjectOpenHashMap<TagKey<Item>> levelTags;

	public ToolTypeExtension(IToolType toolType)
	{
		this.toolType = toolType;
		this.levelTags = new Int2ObjectOpenHashMap<>();
	}

	/***
	 *
	 * @param item
	 * @return -1 mean be fallback level
	 */
	public int getCustomLevel(ItemStack item)
	{
		for (var i = 0; i <= Constants.MAX_BUILDING_LEVEL; i++)
		{
			if (item.is(this.getItemCustomLevelTag(i)))
			{
				return i;
			}

		}

		return -1;
	}

	public boolean isCustomTool(ItemStack itemStack)
	{
		int level = this.getCustomLevel(itemStack);

		if (level == -1)
		{
			return itemStack.is(this.getItemCustomTag());
		}
		else
		{
			return true;
		}

	}

	public IToolType getToolType()
	{
		return this.toolType;
	}

	public TagKey<Item> getItemCustomTag()
	{
		if (this.itemTag == null)
		{
			String path = "custom_tools/" + this.getToolType().getName().toLowerCase();
			this.itemTag = ItemTags.create(MineColoniesTweaks.rl(path));
		}

		return this.itemTag;
	}

	public TagKey<Item> getItemCustomLevelTag(int level)
	{
		return this.levelTags.computeIfAbsent(level, l ->
		{
			ResourceLocation base = this.getItemCustomTag().location();
			String path = base.getPath() + "/" + l;
			return ItemTags.create(new ResourceLocation(base.getNamespace(), path));
		});

	}

}
