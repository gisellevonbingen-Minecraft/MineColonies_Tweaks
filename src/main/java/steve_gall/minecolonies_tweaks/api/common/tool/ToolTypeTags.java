package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class ToolTypeTags
{
	private static final Map<String, TagKey<Item>> CUSTOM_ITEMS = new HashMap<>();
	private static final Map<String, Int2ObjectOpenHashMap<TagKey<Item>>> CUSTOM_LEVELS = new HashMap<>();
	private static final Map<String, TagKey<Item>> BLACKLISTS = new HashMap<>();

	public static final String CUSTOM_PREFIX = "custom_tools";
	public static final String BLACKLIST_PREFIX = "tool_blacklists";
	public static final TagKey<Item> BLACKLIST_ALL = getBlacklist("all");

	@NotNull
	public static TagKey<Item> getCustomItem(@NotNull String toolTypeName)
	{
		return CUSTOM_ITEMS.computeIfAbsent(toolTypeName, n ->
		{
			var path = CUSTOM_PREFIX + "/" + n.toLowerCase();
			return ItemTags.create(MineColoniesTweaks.rl(path));
		});
	}

	@NotNull
	public static TagKey<Item> getCustomLevel(@NotNull String toolTypeName, int level)
	{
		return CUSTOM_LEVELS.computeIfAbsent(toolTypeName, n -> new Int2ObjectOpenHashMap<>()).computeIfAbsent(level, l ->
		{
			var path = CUSTOM_PREFIX + "/" + toolTypeName.toLowerCase() + "/" + l;
			return ItemTags.create(MineColoniesTweaks.rl(path));
		});
	}

	public static boolean isInBlacklist(@NotNull ItemStack stack, @NotNull String toolTypeName)
	{
		return stack.is(BLACKLIST_ALL) || stack.is(getBlacklist(toolTypeName));
	}

	@NotNull
	public static TagKey<Item> getBlacklist(@NotNull String toolTypeName)
	{
		return BLACKLISTS.computeIfAbsent(toolTypeName, n ->
		{
			var path = BLACKLIST_PREFIX + "/" + n;
			return ItemTags.create(MineColoniesTweaks.rl(path));
		});
	}

	private ToolTypeTags()
	{

	}

}
