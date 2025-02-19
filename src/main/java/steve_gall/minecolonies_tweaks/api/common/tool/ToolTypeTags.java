package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class ToolTypeTags
{
	private static final Map<String, TagKey<Item>> CUSTOM_ITEMS = new HashMap<>();
	private static final Map<String, Int2ObjectOpenHashMap<TagKey<Item>>> CUSTOM_LEVELS = new HashMap<>();
	private static final Map<String, TagKey<Item>> BLACKLISTS = new HashMap<>();

	@NotNull
	public static TagKey<Item> getCustomItem(@NotNull String toolTypeName)
	{
		return CUSTOM_ITEMS.computeIfAbsent(toolTypeName, n ->
		{
			var path = "custom_tools/" + n.toLowerCase();
			return ItemTags.create(MineColoniesTweaks.rl(path));
		});
	}

	@NotNull
	public static TagKey<Item> getCustomLevel(@NotNull String toolTypeName, int level)
	{
		return CUSTOM_LEVELS.computeIfAbsent(toolTypeName, n -> new Int2ObjectOpenHashMap<>()).computeIfAbsent(level, l ->
		{
			var path = "custom_tools/" + toolTypeName.toLowerCase() + "/" + l;
			return ItemTags.create(MineColoniesTweaks.rl(path));
		});
	}

	@NotNull
	public static TagKey<Item> getBlacklist(@NotNull String toolTypeName)
	{
		return BLACKLISTS.computeIfAbsent(toolTypeName, n ->
		{
			var path = "tool_blacklists/" + n;
			return ItemTags.create(MineColoniesTweaks.rl(path));
		});
	}

	private ToolTypeTags()
	{

	}

}
