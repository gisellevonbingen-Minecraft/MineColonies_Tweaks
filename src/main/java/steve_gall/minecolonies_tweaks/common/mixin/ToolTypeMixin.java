package steve_gall.minecolonies_tweaks.common.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.minecolonies.api.util.constant.IToolType;
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

	@Shadow
	private static Map<String, IToolType> tools;

	static
	{
		Gson gson = new Gson();

		for (String raw : MineColoniesTweaksConfigCommon.INSTANCE.tools.customTypes.get())
		{
			var json = gson.fromJson(raw, JsonObject.class);
			CustomToolTypeData data = new CustomToolTypeData(json);
			addValue(data);
		}

	}

	@Inject(method = "<clinit>", at = @At(value = "TAIL"), cancellable = true)
	private static void clinit(CallbackInfo ci)
	{
		for (CustomToolTypeData data : CustomToolTypeData.list())
		{
			tools.put(data.getName(), data.getToolType());
		}

	}

	@Invoker("<init>")
	private static ToolType init(String internalName, int internalId, String name, boolean variableMaterials, Component displayName)
	{
		throw new AssertionError();
	}

	private static ToolType addValue(CustomToolTypeData data)
	{
		List<ToolType> values = new ArrayList<>(Arrays.asList(ToolTypeMixin.$VALUES));
		ToolType value = init(data.getName().toUpperCase(), values.get(values.size() - 1).ordinal() + 1, data.getName(), data.hasVariableMaterials(), data.getDisplayName());

		values.add(value);
		data.pair(value);
		ToolTypeMixin.$VALUES = values.toArray(new ToolType[0]);
		CustomToolTypeData.register(data);

		return value;
	}

}
