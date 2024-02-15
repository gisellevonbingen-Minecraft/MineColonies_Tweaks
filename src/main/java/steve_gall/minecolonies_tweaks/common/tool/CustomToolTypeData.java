package steve_gall.minecolonies_tweaks.common.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.IToolType;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.common.MineColoniesTweaks;

public class CustomToolTypeData
{
	private static final Map<String, CustomToolTypeData> MAP = new HashMap<>();
	private static final List<CustomToolTypeData> LIST = new ArrayList<>();

	public static void register(CustomToolTypeData data)
	{
		MAP.put(data.getName(), data);
		LIST.add(data);

		MineColoniesTweaks.LOGGER.info("CustomToolData Added: " + data.getName());
	}

	public static Map<String, CustomToolTypeData> map()
	{
		return Collections.unmodifiableMap(MAP);
	}

	public static List<CustomToolTypeData> list()
	{
		return Collections.unmodifiableList(LIST);
	}

	@Nullable
	public static CustomToolTypeData find(IToolType toolType)
	{
		return toolType != null ? find(toolType.getName()) : null;
	}

	@Nullable
	public static CustomToolTypeData find(String name)
	{
		return MAP.get(name);
	}

	private final String name;
	private final boolean hasVariableMaterials;
	private final String translationKey;

	private final Optional<Integer> defaultLevel;

	private IToolType toolType;
	private TagKey<Item> itemTag;
	private final Int2ObjectOpenHashMap<TagKey<Item>> levelTags;

	public CustomToolTypeData(JsonObject json)
	{
		this.name = GsonHelper.getAsString(json, "name");
		this.hasVariableMaterials = GsonHelper.getAsBoolean(json, "hasVariableMaterials", false);
		this.translationKey = GsonHelper.getAsString(json, "translationKey", MineColoniesTweaks.MOD_ID + ".custom_tooltype." + this.name);

		if (json.has("defaultLevel"))
		{
			this.defaultLevel = Optional.of(GsonHelper.getAsInt(json, "defaultLevel"));
		}
		else
		{
			this.defaultLevel = Optional.empty();
		}

		this.levelTags = new Int2ObjectOpenHashMap<>();
	}

	public JsonObject toObject()
	{
		var json = new JsonObject();
		json.addProperty("name", this.getName());
		json.addProperty("hasVariableMaterials", this.hasVariableMaterials());
		json.addProperty("translationKey", this.getTranslationKey());
		return json;
	}

	public String getName()
	{
		return this.name;
	}

	public boolean hasVariableMaterials()
	{
		return this.hasVariableMaterials;
	}

	public Component getDisplayName()
	{
		return Component.translatable(this.translationKey);
	}

	public String getTranslationKey()
	{
		return this.translationKey;
	}

	public TagKey<Item> getItemTag()
	{
		if (this.itemTag == null)
		{
			String path = "custom_tools/" + this.getName().toLowerCase();
			this.itemTag = ItemTags.create(MineColoniesTweaks.rl(path));
		}

		return this.itemTag;
	}

	public TagKey<Item> getLevelTag(int level)
	{
		return this.levelTags.computeIfAbsent(level, l ->
		{
			ResourceLocation base = this.getItemTag().location();
			String path = base.getPath() + "/" + l;
			return ItemTags.create(new ResourceLocation(base.getNamespace(), path));
		});
	}

	/***
	 *
	 * @param item
	 * @return -1 mean be fallback level
	 */
	public int getLevel(ItemStack item)
	{
		for (var i = 0; i <= Constants.MAX_BUILDING_LEVEL; i++)
		{
			if (item.is(this.getLevelTag(i)))
			{
				return i;
			}

		}

		return -1;
	}

	public boolean isTool(ItemStack itemStack)
	{
		int level = this.getLevel(itemStack);

		if (level == -1)
		{
			return itemStack.is(this.getItemTag());
		}
		else
		{
			return true;
		}

	}

	public Optional<Integer> getDefaultLevel()
	{
		return this.defaultLevel;
	}

	public void pair(IToolType toolType)
	{
		if (toolType == null)
		{
			throw new NullPointerException("toolType");
		}
		else if (this.toolType != null)
		{
			throw new IllegalCallerException("Arleay paired");
		}
		else
		{
			this.toolType = toolType;
		}

	}

	public IToolType getToolType()
	{
		return this.toolType;
	}

}
