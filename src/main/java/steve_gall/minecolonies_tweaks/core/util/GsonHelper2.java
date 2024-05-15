package steve_gall.minecolonies_tweaks.core.util;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

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

	private GsonHelper2()
	{

	}

}
