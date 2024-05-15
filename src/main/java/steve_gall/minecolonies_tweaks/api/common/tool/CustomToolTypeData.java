package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.minecolonies.api.util.constant.IToolType;
import com.minecolonies.api.util.constant.ToolType;

import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.MinecraftForge;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigCommon;
import steve_gall.minecolonies_tweaks.core.util.GsonHelper2;

public class CustomToolTypeData
{
	private static final Map<String, CustomToolTypeData> MAP = new HashMap<>();
	private static final List<CustomToolTypeData> LIST = new ArrayList<>();

	public static void init()
	{
		var gson = new Gson();

		for (var raw : MineColoniesTweaksConfigCommon.INSTANCE.tools.customTypes.get())
		{
			var json = gson.fromJson(raw, JsonObject.class);
			var builder = new CustomToolTypeData.Builder(json);
			register(builder.build());
		}

		MinecraftForge.EVENT_BUS.post(new CustomToolTypeRegisterEvent(CustomToolTypeData::register));
	}

	private static void register(CustomToolTypeData data)
	{
		if (data == null)
		{
			throw new NullPointerException("data");
		}

		MAP.put(data.getName(), data);
		LIST.add(data);

		MineColoniesTweaks.LOGGER.info("CustomToolTypeData Added: " + data.getName());
	}

	@NotNull
	public static Map<String, CustomToolTypeData> map()
	{
		return Collections.unmodifiableMap(MAP);
	}

	@NotNull
	public static List<CustomToolTypeData> list()
	{
		return Collections.unmodifiableList(LIST);
	}

	@Nullable
	public static CustomToolTypeData find(@Nullable IToolType toolType)
	{
		return toolType != null ? find(toolType.getName()) : null;
	}

	@Nullable
	public static CustomToolTypeData find(@NotNull String name)
	{
		return MAP.get(name);
	}

	@NotNull
	private final String name;

	private final boolean hasVariableMaterials;
	@NotNull
	private final String translationKey;
	@NotNull
	private final Optional<Integer> defaultLevel;

	private ToolType toolType;

	private CustomToolTypeData(@NotNull Builder builder)
	{
		this.name = builder.name;
		this.hasVariableMaterials = builder.hasVariableMaterials;
		this.translationKey = builder.translationKey.orElseGet(() -> getFallbackTranslationKey(this.name));
		this.defaultLevel = builder.defaultLevel;
	}

	@NotNull
	public static String getFallbackTranslationKey(@Nullable String name)
	{
		return MineColoniesTweaks.MOD_ID + ".custom_tooltype." + name;
	}

	public static class Builder
	{
		@NotNull
		private final String name;

		private boolean hasVariableMaterials = false;
		@NotNull
		private Optional<String> translationKey = Optional.empty();
		private Optional<Integer> defaultLevel = Optional.empty();

		public Builder(@NotNull String name)
		{
			this.name = name;
		}

		public Builder(@NotNull CustomToolTypeData data)
		{
			this.name = data.name;
			this.hasVariableMaterials = data.hasVariableMaterials;
			this.translationKey = Optional.of(data.translationKey);
			this.defaultLevel = data.defaultLevel;
		}

		public Builder(@NotNull JsonObject json)
		{
			this.name = GsonHelper.getAsString(json, "name");
			this.hasVariableMaterials = GsonHelper.getAsBoolean(json, "hasVariableMaterials", false);
			this.translationKey = GsonHelper2.of(json, "translationKey", GsonHelper::getAsString);
			this.defaultLevel = GsonHelper2.of(json, "defaultLevel", GsonHelper::getAsInt);
		}

		@NotNull
		public JsonObject toObject()
		{
			var json = new JsonObject();
			json.addProperty("name", this.name());
			json.addProperty("hasVariableMaterials", this.hasVariableMaterials());
			GsonHelper2.ifPresent("translationKey", this.translationKey(), json::addProperty);
			GsonHelper2.ifPresent("defaultLevel", this.defaultLevel(), json::addProperty);
			return json;
		}

		@NotNull
		public String name()
		{
			return this.name;
		}

		public boolean hasVariableMaterials()
		{
			return this.hasVariableMaterials;
		}

		public Builder hasVariableMaterials(boolean hasVariableMaterials)
		{
			this.hasVariableMaterials = hasVariableMaterials;
			return this;
		}

		@NotNull
		public Optional<String> translationKey()
		{
			return this.translationKey;
		}

		public Builder translationKey(@NotNull Optional<String> translationKey)
		{
			this.translationKey = translationKey;
			return this;
		}

		@NotNull
		public Optional<Integer> defaultLevel()
		{
			return this.defaultLevel;
		}

		public Builder defaultLevel(@NotNull Optional<Integer> defaultLevel)
		{
			this.defaultLevel = defaultLevel;
			return this;
		}

		@NotNull
		public CustomToolTypeData build()
		{
			return new CustomToolTypeData(this);
		}

	}

	@NotNull
	public String getName()
	{
		return this.name;
	}

	public boolean hasVariableMaterials()
	{
		return this.hasVariableMaterials;
	}

	@NotNull
	public Component getDisplayName()
	{
		return Component.translatable(this.translationKey);
	}

	@NotNull
	public String getTranslationKey()
	{
		return this.translationKey;
	}

	@NotNull
	public Optional<Integer> getDefaultLevel()
	{
		return this.defaultLevel;
	}

	public void pair(@NotNull ToolType toolType)
	{
		if (this.toolType != null)
		{
			throw new IllegalCallerException("Already paired");
		}
		else if (toolType == null)
		{
			throw new NullPointerException("toolType");
		}
		else
		{
			this.toolType = toolType;
		}

	}

	@NotNull
	public ToolType getToolType()
	{
		if (this.toolType == null)
		{
			throw new IllegalCallerException("Not paired");
		}

		return this.toolType;
	}

}
