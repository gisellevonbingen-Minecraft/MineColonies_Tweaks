package steve_gall.minecolonies_tweaks.common.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.minecolonies.api.util.constant.ToolType;

import net.minecraft.network.chat.Component;
import steve_gall.minecolonies_tweaks.common.config.MineColoniesTweaksConfigCommon;
import steve_gall.minecolonies_tweaks.common.tool.CustomToolTypeData;

@Mixin(value = ToolType.class, remap = false)
public abstract class ToolTypeMixin
{
	@Final
	@Mutable
	@Shadow
	private static ToolType[] $VALUES;

	static
	{
		Gson gson = new Gson();

		for (String raw : MineColoniesTweaksConfigCommon.INSTANCE.tools.add.get())
		{
			var json = gson.fromJson(raw, JsonObject.class);
			CustomToolTypeData data = new CustomToolTypeData(json);
			addValue(data);
		}

	}

	@Invoker("<init>")
	public static ToolType init(String internalName, int internalId, String name, boolean variableMaterials, Component displayName)
	{
		throw new AssertionError();
	}

	private static ToolType addValue(CustomToolTypeData data)
	{
		List<ToolType> values = new ArrayList<>(Arrays.asList(ToolTypeMixin.$VALUES));
		ToolType value = init(data.getName().toLowerCase(), values.get(values.size() - 1).ordinal() + 1, data.getName(), data.hasVariableMaterials(), data.getDisplayName());

		values.add(value);
		ToolTypeMixin.$VALUES = values.toArray(new ToolType[0]);
		CustomToolTypeData.register(data);

		return value;
	}

}
