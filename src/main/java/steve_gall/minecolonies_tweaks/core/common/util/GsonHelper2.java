package steve_gall.minecolonies_tweaks.core.common.util;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class GsonHelper2
{
	@NotNull
	public static <T> Optional<T> of(@NotNull JsonObject json, @NotNull String memberName, @NotNull BiFunction<JsonObject, String, T> function)
	{
		return json.has(memberName) ? Optional.of(function.apply(json, memberName)) : Optional.empty();
	}

	@NotNull
	public static <T> void ifPresent(@NotNull String memberName, @NotNull Optional<T> value, @NotNull BiConsumer<String, T> consumer)
	{
		value.ifPresent(v -> consumer.accept(memberName, v));
	}

	public static ResourceLocation convertToResourceLocation(@NotNull JsonElement json, @NotNull String memberName, @NotNull String fallbackNamespace)
	{
		if (json.isJsonPrimitive())
		{
			var raw = json.getAsString();

			if (raw.contains(":"))
			{
				return new ResourceLocation(raw);
			}
			else
			{
				return new ResourceLocation(fallbackNamespace, raw);
			}

		}
		else
		{
			throw new JsonSyntaxException("Expected " + memberName + " to be a ResourceLocation, was " + GsonHelper.getType(json));
		}

	}

	public static ResourceLocation getAsResourceLocation(@NotNull JsonObject json, @NotNull String memberName)
	{
		return getAsResourceLocation(json, memberName, ResourceLocation.DEFAULT_NAMESPACE);
	}

	public static ResourceLocation getAsResourceLocation(@NotNull JsonObject json, @NotNull String memberName, @NotNull String fallbackNamespace)
	{
		if (json.has(memberName))
		{
			return convertToResourceLocation(json.get(memberName), memberName, fallbackNamespace);
		}
		else
		{
			throw new JsonSyntaxException("Missing " + memberName + ", expected to find a ResourceLocation");
		}

	}

	private GsonHelper2()
	{

	}

}
