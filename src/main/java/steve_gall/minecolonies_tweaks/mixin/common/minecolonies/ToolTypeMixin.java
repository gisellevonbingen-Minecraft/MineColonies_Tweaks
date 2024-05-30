package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.ArrayList;
import java.util.Arrays;
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
import steve_gall.minecolonies_tweaks.api.common.tool.CustomToolType;

@Mixin(value = ToolType.class, remap = false)
public abstract class ToolTypeMixin
{
	@Final
	@Mutable
	@Shadow(remap = false)
	private static ToolType[] $VALUES;

	@Shadow(remap = false)
	private static Map<String, IToolType> tools;

	static
	{
		CustomToolType.init();
		CustomToolType.list().forEach(ToolTypeMixin::addValue);
	}

	@Inject(method = "<clinit>", remap = false, at = @At(value = "TAIL"), cancellable = true)
	private static void clinit(CallbackInfo ci)
	{
		for (var data : CustomToolType.list())
		{
			tools.put(data.getName().getPath(), data.getToolType());
		}

	}

	@Invoker(value = "<init>", remap = false)
	private static ToolType init(String internalName, int internalId, String name, boolean variableMaterials, Component displayName)
	{
		throw new AssertionError();
	}

	private static ToolType addValue(CustomToolType data)
	{
		var values = new ArrayList<>(Arrays.asList(ToolTypeMixin.$VALUES));
		var path = data.getName().getPath();
		var value = init(path.toUpperCase(), values.get(values.size() - 1).ordinal() + 1, path, data.hasVariableMaterials(), data.getDisplayName());

		values.add(value);
		data.pair(value);
		ToolTypeMixin.$VALUES = values.toArray(new ToolType[0]);

		return value;
	}

}
