package steve_gall.minecolonies_tweaks.api.common.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.minecolonies.api.util.constant.IToolType;
import com.minecolonies.api.util.constant.ToolType;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigCommon;

public class CustomToolType
{
	private static boolean INITIALIZED = false;
	private static final Map<String, CustomToolType> MAP = new HashMap<>();
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
			var gson = new Gson();

			for (var raw : MineColoniesTweaksConfigCommon.INSTANCE.tools.customTypes.get())
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
		catch (Exception e)
		{
			MineColoniesTweaks.LOGGER.error("Exception during load CustomToolType", e);
		}

		MinecraftForge.EVENT_BUS.post(new CustomToolTypeRegisterEvent(CustomToolType::register));
	}

	private static void register(CustomToolType data)
	{
		if (data == null)
		{
			throw new NullPointerException("data");
		}

		var name = data.getName();
		var path = name.getPath();
		var prev = MAP.get(path);

		if (prev != null)
		{
			throw new IllegalArgumentException("Name '" + path + "' is already registered from: " + prev.getName().getNamespace());
		}

		MAP.put(path, data);
		LIST.add(data);

		MineColoniesTweaks.LOGGER.info("CustomToolTypeData Added: " + name);
	}

	@NotNull
	public static Map<String, CustomToolType> map()
	{
		return Collections.unmodifiableMap(MAP);
	}

	@NotNull
	public static List<CustomToolType> list()
	{
		return Collections.unmodifiableList(LIST);
	}

	@Nullable
	public static CustomToolType find(@Nullable IToolType toolType)
	{
		return toolType != null ? find(toolType.getName()) : null;
	}

	@Nullable
	public static CustomToolType find(@NotNull String name)
	{
		return MAP.get(name);
	}

	@NotNull
	private final ResourceLocation name;

	@Nullable
	private Component displayName;

	@Nullable
	private ToolType toolType;

	public CustomToolType(@NotNull ResourceLocation name)
	{
		this.name = name;
	}

	@NotNull
	public static String getFallbackTranslationKey(@NotNull String name)
	{
		return MineColoniesTweaks.MOD_ID + ".custom_tooltype." + name;
	}

	@NotNull
	public final ResourceLocation getName()
	{
		return this.name;
	}

	public boolean hasVariableMaterials()
	{
		return false;
	}

	protected Component createDisplayName()
	{
		return Component.translatable(getFallbackTranslationKey(this.getName().getPath()));
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

	public final void pair(@NotNull ToolType toolType)
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
	public final ToolType getToolType()
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
