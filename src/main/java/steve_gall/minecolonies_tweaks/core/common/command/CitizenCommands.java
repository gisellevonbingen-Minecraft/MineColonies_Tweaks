package steve_gall.minecolonies_tweaks.core.common.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.constant.translation.CommandTranslationConstants;
import com.minecolonies.core.commands.CommandArgumentNames;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.minecolonies.core.datalistener.model.Disease;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.CitizenDiseaseHandlerAccessor;

public class CitizenCommands
{
	public static LiteralArgumentBuilder<CommandSourceStack> register()
	{
		var command = Commands.literal("citizens");
		command.then(SaturationCommands.register());
		command.then(DiseaseCommands.register());

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

		private static ArgumentBuilder<CommandSourceStack, ?> full()
		{
			return executes(Commands.literal("full"), true, (context, citizen) ->
			{
				citizen.getCitizenData().setSaturation(ICitizenData.MAX_SATURATION);
				citizen.getCitizenData().setJustAte(true);
				return true;
			}, (context, citizens) -> context.getSource().sendSuccess(() -> Component.literal("Done"), true));
		}

		private static ArgumentBuilder<CommandSourceStack, ?> empty()
		{
			return executes(Commands.literal("empty"), true, (context, citizen) ->
			{
				citizen.getCitizenData().setSaturation(0.0D);
				citizen.getCitizenData().setJustAte(false);
				return true;
			}, (context, citizens) -> context.getSource().sendSuccess(() -> Component.literal("Done"), true));
		}

	}

	public static class DiseaseCommands
	{
		public static ArgumentBuilder<CommandSourceStack, ?> register()
		{
			var command = Commands.literal("disease");
			command.then(disease());
			command.then(cure());

			return command;
		}

		private static ArgumentBuilder<CommandSourceStack, ?> disease()
		{
			var diseaseArgument = IMCCommand.newArgument("disease", DiseaseArgumentType.instance());
			executes(diseaseArgument, true, (context, citizen) ->
			{
				var disease = context.getArgument("disease", Disease.class);
				((CitizenDiseaseHandlerAccessor) citizen.getCitizenData().getCitizenDiseaseHandler()).setImmunityTicks(0);
				citizen.getCitizenData().getCitizenDiseaseHandler().setDisease(disease);
				return true;
			}, (context, citizens) -> context.getSource().sendSuccess(() -> Component.literal("Done"), true));
			return Commands.literal("set").then(diseaseArgument);
		}

		private static ArgumentBuilder<CommandSourceStack, ?> cure()
		{
			return executes(Commands.literal("cure"), true, (context, citizen) ->
			{
				citizen.getCitizenData().getCitizenDiseaseHandler().cure();
				return true;
			}, (context, citizens) -> context.getSource().sendSuccess(() -> Component.literal("Done"), true));
		}

	}

	public static ArgumentBuilder<CommandSourceStack, ?> executes(ArgumentBuilder<CommandSourceStack, ?> builder, boolean needPermission, BiPredicate<CommandContext<CommandSourceStack>, AbstractEntityCitizen> func, BiConsumer<CommandContext<CommandSourceStack>, Collection<AbstractEntityCitizen>> callback)
	{
		return builder//
				.then(IMCCommand.newArgument(CommandArgumentNames.COLONYID_ARG, IntegerArgumentType.integer(1))//
						.executes(context -> runAll(context, needPermission, func, callback))//
						.then(IMCCommand.newArgument(CommandArgumentNames.CITIZENID_ARG, IntegerArgumentType.integer(1))//
								.executes(context -> runSingle(context, needPermission, func, callback))))//
		;
	}

	public static IColony check(CommandContext<CommandSourceStack> context, boolean needPermission)
	{
		if (needPermission && !context.getSource().hasPermission(Commands.LEVEL_GAMEMASTERS))
		{
			context.getSource().sendSuccess(() -> Component.translatable(CommandTranslationConstants.COMMAND_REQUIRES_OP), true);
			return null;
		}

		var colonyID = IntegerArgumentType.getInteger(context, CommandArgumentNames.COLONYID_ARG);
		var colony = IColonyManager.getInstance().getColonyByDimension(colonyID, context.getSource().getLevel().dimension());

		if (colony == null)
		{
			context.getSource().sendSuccess(() -> Component.translatable(CommandTranslationConstants.COMMAND_COLONY_ID_NOT_FOUND, colonyID), true);
			return null;
		}

		if (!context.getSource().hasPermission(Commands.LEVEL_OWNERS))
		{
			context.getSource().sendSuccess(() -> Component.translatable(CommandTranslationConstants.COMMAND_DISABLED_IN_CONFIG), true);
			return null;
		}

		return colony;
	}

	public static int runAll(CommandContext<CommandSourceStack> context, boolean needPermission, BiPredicate<CommandContext<CommandSourceStack>, AbstractEntityCitizen> func, BiConsumer<CommandContext<CommandSourceStack>, Collection<AbstractEntityCitizen>> callback)
	{
		var colony = check(context, needPermission);

		if (colony == null)
		{
			return 0;
		}

		var list = new ArrayList<AbstractEntityCitizen>();

		for (var citizenData : colony.getCitizenManager().getCitizens())
		{
			var optionalEntityCitizen = citizenData.getEntity();

			if (!optionalEntityCitizen.isPresent())
			{
				continue;
			}

			var entityCitizen = optionalEntityCitizen.get();

			if (func.test(context, entityCitizen))
			{
				list.add(entityCitizen);
			}

		}

		callback.accept(context, list);
		return list.size();
	}

	public static int runSingle(CommandContext<CommandSourceStack> context, boolean needPermission, BiPredicate<CommandContext<CommandSourceStack>, AbstractEntityCitizen> func, BiConsumer<CommandContext<CommandSourceStack>, Collection<AbstractEntityCitizen>> callback)
	{
		var colony = check(context, needPermission);

		if (colony == null)
		{
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

		var entityCitizen = optionalEntityCitizen.get();

		if (func.test(context, entityCitizen))
		{
			callback.accept(context, Collections.singletonList(entityCitizen));
			return 1;
		}
		else
		{
			callback.accept(context, Collections.emptyList());
			return 0;
		}

	}

}
