package steve_gall.minecolonies_tweaks.core.common.command;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.minecolonies.api.colony.IColonyManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class DiseaseArgumentType implements ArgumentType<String>
{
	private static final DiseaseArgumentType INSTANCE = new DiseaseArgumentType();
	public static final DynamicCommandExceptionType ERROR_UNKNOWN_DISEASE = new DynamicCommandExceptionType(object ->
	{
		return Component.literal("Unknown disease name: " + object);
	});

	public static DiseaseArgumentType instance()
	{
		return INSTANCE;
	}

	public static Stream<String> iterateIds()
	{
		return IColonyManager.getInstance().getCompatibilityManager().getDiseases().stream().map(disease -> disease.getName());
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException
	{
		var id = reader.readString();
		var disease = IColonyManager.getInstance().getCompatibilityManager().getDisease(id);

		if (disease == null)
		{
			throw ERROR_UNKNOWN_DISEASE.createWithContext(reader, id);
		}

		return disease.getName();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		return SharedSuggestionProvider.suggest(iterateIds(), builder);
	}

	@Override
	public Collection<String> getExamples()
	{
		return iterateIds().toList();
	}

}
