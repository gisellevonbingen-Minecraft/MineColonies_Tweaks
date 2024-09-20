package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.util.constant.Constants;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class ToolTypeExtension
{
	private static Map<EquipmentTypeEntry, ToolTypeExtension> MAP = new HashMap<>();

	@NotNull
	public static ToolTypeExtension from(@NotNull EquipmentTypeEntry toolType)
	{
		return MAP.computeIfAbsent(toolType, ToolTypeExtension::new);
	}

	public static String getTagNamespace(ResourceLocation name)
	{
		var namespace = name.getNamespace();

		if (namespace.equals(Constants.MOD_ID))
		{
			return MineColoniesTweaks.MOD_ID;
		}
		else
		{
			return namespace;
		}

	}

	@NotNull
	public static TagKey<Item> getItemCustomTag(@NotNull ResourceLocation name)
	{
		var path = "custom_tools/" + name.getPath().toLowerCase();
		return ItemTags.create(new ResourceLocation(getTagNamespace(name), path));
	}

	@NotNull
	public static TagKey<Item> getItemCustomLevelTag(@NotNull ResourceLocation name, int level)
	{
		var path = "custom_tools/" + name.getPath().toLowerCase() + "/" + level;
		return ItemTags.create(new ResourceLocation(getTagNamespace(name), path));
	}

	@NotNull
	private final EquipmentTypeEntry toolType;
	@Nullable
	private final CustomToolType customToolType;

	private TagKey<Item> itemTag;
	private final Int2ObjectOpenHashMap<TagKey<Item>> levelTags;

	private ToolTypeExtension(@NotNull EquipmentTypeEntry toolType)
	{
		this.toolType = toolType;
		this.customToolType = CustomToolType.find(toolType.getRegistryName());
		this.levelTags = new Int2ObjectOpenHashMap<>();
	}

	private int getTagLevel(@NotNull ItemStack item)
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

	/***
	 *
	 * @param item
	 * @return -1 mean be fallback level
	 */
	public int getCustomLevel(@NotNull ItemStack item)
	{
		var tagLevel = this.getTagLevel(item);

		if (tagLevel > -1)
		{
			return tagLevel;
		}

		var custom = this.getCustomToolType();

		if (custom != null)
		{
			var customLevel = custom.getToolLevel(item);

			if (customLevel > -1)
			{
				return customLevel;
			}

		}

		return -1;
	}

	public boolean isCustomTool(@NotNull ItemStack itemStack)
	{
		if (itemStack.is(this.getItemCustomTag()))
		{
			return true;
		}

		var tagLevel = this.getTagLevel(itemStack);

		if (tagLevel > -1)
		{
			return true;
		}

		var custom = this.getCustomToolType();

		if (custom != null && custom.isTool(itemStack))
		{
			return true;
		}

		return false;
	}

	@NotNull
	public EquipmentTypeEntry getToolType()
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
			this.itemTag = getItemCustomTag(this.getToolType().getRegistryName());
		}

		return this.itemTag;
	}

	@NotNull
	public TagKey<Item> getItemCustomLevelTag(int level)
	{
		return this.levelTags.computeIfAbsent(level, l ->
		{
			return getItemCustomLevelTag(this.getToolType().getRegistryName(), l);
		});

	}

}
