package steve_gall.minecolonies_tweaks.core.common.command;

import java.util.function.ToIntBiFunction;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.constant.translation.CommandTranslationConstants;
import com.minecolonies.core.commands.CommandArgumentNames;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CitizenCommands
{
	public static LiteralArgumentBuilder<CommandSourceStack> register()
	{
		var command = Commands.literal("citizens");
		command.then(SaturationCommands.register());

		return command;
	}

	public static class SaturationCommands
	{
		public static LiteralArgumentBuilder<CommandSourceStack> register()
		{
			var command = Commands.literal("saturation");
			command.then(full());
			command.then(empty());

			return command;
		}

		private static LiteralArgumentBuilder<CommandSourceStack> full()
		{
			return literal("full", (context, citizen) ->
			{
				citizen.getCitizenData().setSaturation(ICitizenData.MAX_SATURATION);
				context.getSource().sendSuccess(() -> Component.literal("Done"), true);
				return 1;
			});
		}

		private static LiteralArgumentBuilder<CommandSourceStack> empty()
		{
			return literal("empty", (context, citizen) ->
			{
				citizen.getCitizenData().setSaturation(0.0D);
				citizen.getCitizenData().setJustAte(false);
				context.getSource().sendSuccess(() -> Component.literal("Done"), true);
				return 1;
			});
		}

	}

	public static LiteralArgumentBuilder<CommandSourceStack> literal(String name, ToIntBiFunction<CommandContext<CommandSourceStack>, AbstractEntityCitizen> func)
	{
		return Commands.literal(name)//
				.then(IMCCommand.newArgument(CommandArgumentNames.COLONYID_ARG, IntegerArgumentType.integer(1))//
						.then(IMCCommand.newArgument(CommandArgumentNames.CITIZENID_ARG, IntegerArgumentType.integer(1))//
								.executes(context -> run(context, func))))//
		;
	}

	public static int run(CommandContext<CommandSourceStack> context, ToIntBiFunction<CommandContext<CommandSourceStack>, AbstractEntityCitizen> func)
	{
		var colonyID = IntegerArgumentType.getInteger(context, CommandArgumentNames.COLONYID_ARG);
		var colony = IColonyManager.getInstance().getColonyByDimension(colonyID, context.getSource().getLevel().dimension());

		if (colony == null)
		{
			context.getSource().sendSuccess(() -> Component.translatable(CommandTranslationConstants.COMMAND_COLONY_ID_NOT_FOUND, colonyID), true);
			return 0;
		}

		if (!context.getSource().hasPermission(Commands.LEVEL_OWNERS))
		{
			context.getSource().sendSuccess(() -> Component.translatable(CommandTranslationConstants.COMMAND_DISABLED_IN_CONFIG), true);
			return 0;
		}

		var citizenData = colony.getCitizenManager().getCivilian(IntegerArgumentType.getInteger(context, CommandArgumentNames.CITIZENID_ARG));

		if (citizenData == null)
		{
			context.getSource().sendSuccess(() -> Component.translatable(CommandTranslationConstants.COMMAND_CITIZEN_NOT_FOUND), true);
			return 0;
		}

		var optionalEntityCitizen = citizenData.getEntity();

		if (!optionalEntityCitizen.isPresent())
		{
			context.getSource().sendSuccess(() -> Component.translatable(CommandTranslationConstants.COMMAND_CITIZEN_NOT_LOADED), true);
			return 0;
		}

		return func.applyAsInt(context, optionalEntityCitizen.get());
	}

}
