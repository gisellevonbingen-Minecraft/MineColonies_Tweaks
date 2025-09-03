package steve_gall.minecolonies_tweaks.api.common.tool;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLPaths;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class CustomToolType
{
	private static boolean INITIALIZED = false;
	private static final Map<ResourceLocation, CustomToolType> MAP = new HashMap<>();
	private static final List<CustomToolType> LIST = new ArrayList<>();

	public static void init()
	{
		if (INITIALIZED)
		{
			return;
		}

		INITIALIZED = true;

		try
		{
			var path = FMLPaths.CONFIGDIR.get().resolve(MineColoniesTweaks.MOD_ID + "-custom_tools.json");

			if (Files.exists(path))
			{
				var gson = new Gson();
				var raws = gson.fromJson(Files.readString(path), JsonArray.class);

				for (var raw : raws)
				{
					try
					{
						var json = gson.fromJson(raw, JsonObject.class);
						var builder = new ConfigToolType.Builder(json);
						register(new ConfigToolType(builder, MineColoniesTweaks.MOD_ID));
					}
					catch (Exception e)
					{
						MineColoniesTweaks.LOGGER.error("Exception during load CustomToolType: " + raw, e);
					}

				}

			}
			else
			{
				Files.writeString(path, new JsonArray().toString());
			}

		}
		catch (Exception e)
		{
			MineColoniesTweaks.LOGGER.error("Exception during load CustomToolType", e);
		}

		ModLoader.postEvent(new CustomToolTypeRegisterEvent(CustomToolType::register));
	}

	private static void register(CustomToolType data)
	{
		if (data == null)
		{
			throw new NullPointerException("data");
		}

		var name = data.getName();
		var prev = MAP.get(name);

		if (prev != null)
		{
			throw new IllegalArgumentException("Name '" + name + "' is already registered");
		}

		MAP.put(name, data);
		LIST.add(data);

		MineColoniesTweaks.LOGGER.info("CustomToolTypeData Added: " + name);
	}

	@NotNull
	public static Map<ResourceLocation, CustomToolType> map()
	{
		return Collections.unmodifiableMap(MAP);
	}

	@NotNull
	public static List<CustomToolType> list()
	{
		return Collections.unmodifiableList(LIST);
	}

	@Nullable
	public static CustomToolType find(@Nullable EquipmentTypeEntry toolType)
	{
		return toolType != null ? find(toolType.getRegistryName()) : null;
	}

	@Nullable
	public static CustomToolType find(@NotNull ResourceLocation name)
	{
		return MAP.get(name);
	}

	@NotNull
	private final ResourceLocation name;

	@Nullable
	private Component displayName;

	@Nullable
	private EquipmentTypeEntry toolType;

	public CustomToolType(@NotNull ResourceLocation name)
	{
		this.name = name;
	}

	@NotNull
	public static String getFallbackTranslationKey(@NotNull ResourceLocation name)
	{
		return MineColoniesTweaks.MOD_ID + ".custom_tooltype." + ToolTypeTags.getTagPath(name).replace('/', '.');
	}

	@NotNull
	public final ResourceLocation getName()
	{
		return this.name;
	}

	protected Component createDisplayName()
	{
		return Component.translatable(getFallbackTranslationKey(this.getName()));
	}

	@NotNull
	public Component getDisplayName()
	{
		if (this.displayName == null)
		{
			this.displayName = this.createDisplayName();
		}

		return this.displayName;
	}

	public final void pair(@NotNull EquipmentTypeEntry toolType)
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
	public final EquipmentTypeEntry getToolType()
	{
		if (this.toolType == null)
		{
			throw new IllegalCallerException("Not paired");
		}

		return this.toolType;
	}

	public int getToolLevel(@NotNull ItemStack stack)
	{
		return -1;
	}

	public boolean isTool(@NotNull ItemStack stack)
	{
		return false;
	}

}
