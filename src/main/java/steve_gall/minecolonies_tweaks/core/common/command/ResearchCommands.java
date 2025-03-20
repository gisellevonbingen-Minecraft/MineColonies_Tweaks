package steve_gall.minecolonies_tweaks.core.common.command;

import java.util.function.ToIntBiFunction;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.util.constant.translation.CommandTranslationConstants;
import com.minecolonies.core.commands.CommandArgumentNames;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import steve_gall.minecolonies_tweaks.core.common.research.LocalResearchTreeExtension;

public class ResearchCommands
{
	public static LiteralArgumentBuilder<CommandSourceStack> register()
	{
		var command = Commands.literal("researches");
		command.then(clear());

		return command;
	}

	private static LiteralArgumentBuilder<CommandSourceStack> clear()
	{
		return literal("clear", true, (context, colony) ->
		{
			if (colony.getResearchManager().getResearchTree() instanceof LocalResearchTreeExtension extension)
			{
				extension.minecolonies_tweaks$resetAll(colony);
				context.getSource().sendSuccess(Component.literal("Done"), true);
				return 1;
			}
			else
			{
				context.getSource().sendSuccess(Component.literal("Fail"), true);
				return 0;
			}
		});
	}

	public static LiteralArgumentBuilder<CommandSourceStack> literal(String name, boolean needPermission, ToIntBiFunction<CommandContext<CommandSourceStack>, IColony> func)
	{
		return Commands.literal(name)//
				.then(IMCCommand.newArgument(CommandArgumentNames.COLONYID_ARG, IntegerArgumentType.integer(1))//
						.executes(context -> run(context, needPermission, func)))//
		;
	}

	public static int run(CommandContext<CommandSourceStack> context, boolean needPermission, ToIntBiFunction<CommandContext<CommandSourceStack>, IColony> func)
	{
		if (needPermission && !context.getSource().hasPermission(Commands.LEVEL_GAMEMASTERS))
		{
			context.getSource().sendSuccess(Component.translatable(CommandTranslationConstants.COMMAND_REQUIRES_OP), true);
			return 0;
		}

		var colonyID = IntegerArgumentType.getInteger(context, CommandArgumentNames.COLONYID_ARG);
		var colony = IColonyManager.getInstance().getColonyByDimension(colonyID, context.getSource().getLevel().dimension());

		if (colony == null)
		{
			context.getSource().sendSuccess(Component.translatable(CommandTranslationConstants.COMMAND_COLONY_ID_NOT_FOUND, colonyID), true);
			return 0;
		}

		if (!context.getSource().hasPermission(Commands.LEVEL_OWNERS))
		{
			context.getSource().sendSuccess(Component.translatable(CommandTranslationConstants.COMMAND_DISABLED_IN_CONFIG), true);
			return 0;
		}

		return func.applyAsInt(context, colony);
	}

}
