package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

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

import com.minecolonies.api.util.constant.IToolType;
import com.minecolonies.api.util.constant.ToolType;

import net.minecraft.network.chat.Component;
import steve_gall.minecolonies_tweaks.api.common.tool.CustomToolTypeData;

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
		CustomToolTypeData.init();
		CustomToolTypeData.list().forEach(ToolTypeMixin::addValue);
	}

	@Inject(method = "<clinit>", at = @At(value = "TAIL"), cancellable = true)
	private static void clinit(CallbackInfo ci)
	{
		for (var data : CustomToolTypeData.list())
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
		var values = new ArrayList<>(Arrays.asList(ToolTypeMixin.$VALUES));
		var value = init(data.getName().toUpperCase(), values.get(values.size() - 1).ordinal() + 1, data.getName(), data.hasVariableMaterials(), data.getDisplayName());

		values.add(value);
		data.pair(value);
		ToolTypeMixin.$VALUES = values.toArray(new ToolType[0]);

		return value;
	}

}
