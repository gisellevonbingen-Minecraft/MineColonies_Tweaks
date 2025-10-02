package steve_gall.minecolonies_tweaks.core.common.command;

import java.util.List;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.research.util.ResearchState;
import com.minecolonies.api.util.constant.translation.CommandTranslationConstants;
import com.minecolonies.core.commands.CommandArgumentNames;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.research.LocalResearchTreeExtension;

public class ResearchCommands
{
	public static LiteralArgumentBuilder<CommandSourceStack> register()
	{
		var command = Commands.literal("researches");
		command.then(reset());
		command.then(complete());

		return command;
	}

	private static LiteralArgumentBuilder<CommandSourceStack> reset()
	{
		return optionalBranch("reset", true, (context, colony, branchId) ->
		{
			var localResearchManager = colony.getResearchManager();

			if (localResearchManager.getResearchTree() instanceof LocalResearchTreeExtension extension)
			{
				if (branchId == null)
				{
					extension.minecolonies_tweaks$resetAll(colony);
				}
				else
				{
					extension.minecolonies_tweaks$reset(colony, branchId);
				}

				localResearchManager.markDirty();
				context.getSource().sendSuccess(Component.literal("Done"), true);
				return 1;
			}
			else
			{
				IMinecoloniesAPI.getInstance().getGlobalResearchTree().getBranchData(null);
				context.getSource().sendSuccess(Component.literal("Fail"), true);
				return 0;
			}
		});
	}

	private static LiteralArgumentBuilder<CommandSourceStack> complete()
	{
		return optionalBranch("complete", true, (context, colony, branchId) ->
		{
			var globalResearchTree = IMinecoloniesAPI.getInstance().getGlobalResearchTree();
			var localResearchManager = colony.getResearchManager();

			if (branchId == null)
			{
				for (var branch : globalResearchTree.getBranches())
				{
					complete(globalResearchTree, colony, branch, globalResearchTree.getPrimaryResearch(branch));
				}

			}
			else
			{
				complete(globalResearchTree, colony, branchId, globalResearchTree.getPrimaryResearch(branchId));
			}

			for (var citizen : colony.getCitizenManager().getCitizens())
			{
				citizen.applyResearchEffects();
			}

			localResearchManager.markDirty();
			context.getSource().sendSuccess(Component.literal("Done"), true);
			return 1;
		});
	}

	private static void complete(IGlobalResearchTree globalResearchTree, IColony colony, ResourceLocation branchId, List<ResourceLocation> researchIds)
	{
		var globalBranchData = globalResearchTree.getBranchData(branchId);
		var localResearchManager = colony.getResearchManager();
		var localResearchTree = localResearchManager.getResearchTree();

		for (var researchId : researchIds)
		{
			var globalResearch = globalResearchTree.getResearch(branchId, researchId);
			globalResearch.startResearch(localResearchTree);

			var localResearch = localResearchTree.getResearch(branchId, researchId);

			if (localResearch.getState() == ResearchState.IN_PROGRESS)
			{
				localResearch.setProgress(globalBranchData.getBaseTime(globalResearch.getDepth()));
			}

			localResearch.research(localResearchManager.getResearchEffects(), localResearchTree);

			complete(globalResearchTree, colony, branchId, globalResearch.getChildren());
		}

	}

	public static LiteralArgumentBuilder<CommandSourceStack> optionalBranch(String name, boolean needPermission, ResearchRunFunction func)
	{
		return Commands.literal(name)//
				.then(IMCCommand.newArgument(CommandArgumentNames.COLONYID_ARG, IntegerArgumentType.integer(1))//
						.executes(context -> runOptionalBranch(context, needPermission, false, func))//
						.then(newArgumentbranchID()//
								.executes(context -> runOptionalBranch(context, needPermission, true, func))))//
		;
	}

	private static RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> newArgumentbranchID()
	{
		return IMCCommand.newArgument("branchID", ResourceLocationArgument.id()).suggests((context, builder) ->
		{
			for (var branchID : IMinecoloniesAPI.getInstance().getGlobalResearchTree().getBranches())
			{
				builder.suggest(branchID.toString());
			}
			return builder.buildFuture();
		});
	}

	public static int runOptionalBranch(CommandContext<CommandSourceStack> context, boolean needPermission, boolean hasBranch, ResearchRunFunction func)
	{
		if (needPermission && !context.getSource().hasPermission(Commands.LEVEL_GAMEMASTERS))
		{
			context.getSource().sendSuccess(Component.translatable(CommandTranslationConstants.COMMAND_REQUIRES_OP), true);
			return 0;
		}
		else if (!context.getSource().hasPermission(Commands.LEVEL_OWNERS))
		{
			context.getSource().sendSuccess(Component.translatable(CommandTranslationConstants.COMMAND_DISABLED_IN_CONFIG), true);
			return 0;
		}

		var colonyID = IntegerArgumentType.getInteger(context, CommandArgumentNames.COLONYID_ARG);
		var colony = IColonyManager.getInstance().getColonyByDimension(colonyID, context.getSource().getLevel().dimension());

		if (colony == null)
		{
			context.getSource().sendSuccess(Component.translatable(CommandTranslationConstants.COMMAND_COLONY_ID_NOT_FOUND, colonyID), true);
			return 0;
		}

		if (hasBranch)
		{
			var branchId = ResourceLocationArgument.getId(context, "branchID");

			if (!IMinecoloniesAPI.getInstance().getGlobalResearchTree().getBranches().contains(branchId))
			{
				context.getSource().sendSuccess(Component.literal("Research Branch Not Found: " + branchId), true);
				return 0;
			}

			return func.applyAsInt(context, colony, branchId);
		}
		else
		{
			return func.applyAsInt(context, colony, null);
		}

	}

	public interface ResearchRunFunction
	{
		int applyAsInt(CommandContext<CommandSourceStack> context, IColony colony, ResourceLocation branchId);
	}

}
