package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.IToolType;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class ToolTypeExtension
{
	private static Map<IToolType, ToolTypeExtension> MAP = new HashMap<>();

	@NotNull
	public static ToolTypeExtension from(@NotNull IToolType toolType)
	{
		return MAP.computeIfAbsent(toolType, ToolTypeExtension::new);
	}

	@NotNull
	public static TagKey<Item> getItemCustomTag(@NotNull String name)
	{
		var path = "custom_tools/" + name.toLowerCase();
		return ItemTags.create(MineColoniesTweaks.rl(path));
	}

	@NotNull
	public static TagKey<Item> getItemCustomLevelTag(@NotNull String name, int level)
	{
		var path = "custom_tools/" + name.toLowerCase() + "/" + level;
		return ItemTags.create(MineColoniesTweaks.rl(path));
	}

	@NotNull
	private final IToolType toolType;
	@Nullable
	private final CustomToolType customToolType;

	private TagKey<Item> itemTag;
	private final Int2ObjectOpenHashMap<TagKey<Item>> levelTags;

	private ToolTypeExtension(@NotNull IToolType toolType)
	{
		this.toolType = toolType;
		this.customToolType = CustomToolType.find(toolType.getName());
		this.levelTags = new Int2ObjectOpenHashMap<>();
	}

	/***
	 *
	 * @param item
	 * @return -1 mean be fallback level
	 */
	public int getCustomLevel(@NotNull ItemStack item)
	{
		var custom = this.getCustomToolType();

		if (custom != null)
		{
			var level = custom.getToolLevel(item);

			if (level > -1)
			{
				return level;
			}

		}

		for (var i = 0; i <= Constants.MAX_BUILDING_LEVEL; i++)
		{
			if (item.is(this.getItemCustomLevelTag(i)))
			{
				return i;
			}

		}

		return -1;
	}

	public boolean isCustomTool(@NotNull ItemStack itemStack)
	{
		var custom = this.getCustomToolType();

		if (custom != null)
		{
			if (custom.isTool(itemStack))
			{
				return true;
			}

		}

		var level = this.getCustomLevel(itemStack);

		if (level == -1)
		{
			return itemStack.is(this.getItemCustomTag());
		}
		else
		{
			return true;
		}

	}

	@NotNull
	public IToolType getToolType()
	{
		return this.toolType;
	}

	@Nullable
	public CustomToolType getCustomToolType()
	{
		return this.customToolType;
	}

	@NotNull
	public TagKey<Item> getItemCustomTag()
	{
		if (this.itemTag == null)
		{
			this.itemTag = getItemCustomTag(this.getToolType().getName());
		}

		return this.itemTag;
	}

	@NotNull
	public TagKey<Item> getItemCustomLevelTag(int level)
	{
		return this.levelTags.computeIfAbsent(level, l ->
		{
			return getItemCustomLevelTag(this.getToolType().getName(), l);
		});

	}

}
