package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.ldtteam.blockui.Pane;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.research.ILocalResearchTree;
import com.minecolonies.core.client.gui.WindowResearchTree;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = WindowResearchTree.class, remap = false)
public abstract class WindowResearchTreeMixin extends Pane
{
	@WrapOperation(method = "<init>", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/ILocalResearchTree.branchFinishedHighestLevel", remap = false))
	private boolean init_branchFinishedHighestLevel(ILocalResearchTree tree, ResourceLocation branch, Operation<Boolean> operation)
	{
		if (this.mc.player.isCreative() || MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get())
		{
			return false;
		}

		return operation.call(tree, branch);
	}

	@WrapOperation(method = "getResearchButtonState", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/IGlobalResearch.getDepth", remap = false))
	private int getResearchButtonState_getDepth(IGlobalResearch research, Operation<Integer> operation)
	{
		if (this.mc.player.isCreative())
		{
			return 0;
		}

		return operation.call(research);
	}

	@WrapOperation(method = {"onButtonClicked", "generateResearchTooltips"}, remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/IGlobalResearch.getDepth", remap = false))
	private int onButtonClicked_getDepth(IGlobalResearch research, Operation<Integer> operation)
	{
		if (this.mc.player.isCreative() || MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get())
		{
			return 0;
		}

		return operation.call(research);
	}

	@WrapOperation(method = "getResearchButtonState", remap = false, at = @At(value = "FIELD", target = "hasMax:Z", remap = false))
	private boolean getResearchButtonState_hasMax(WindowResearchTree self, Operation<Boolean> operation)
	{
		if (MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get())
		{
			return false;
		}

		return operation.call(self);
	}

	@WrapOperation(method = {"drawResearchItem", "drawArrows"}, remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/IGlobalResearch.hasOnlyChild", remap = false))
	private boolean drawResearchItem_hasOnlyChild(IGlobalResearch research, Operation<Boolean> operation)
	{
		if (this.mc.player.isCreative() || MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get())
		{
			return false;
		}

		return operation.call(research);
	}

	@WrapOperation(method = "drawTreeBackground", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/colony/buildings/views/IBuildingView.getBuildingLevel", remap = false))
	private int drawTreeBackground_getBuildingLevel(IBuildingView building, Operation<Integer> operation)
	{
		if (this.mc.player.isCreative())
		{
			return building.getBuildingMaxLevel();
		}

		return operation.call(building);
	}

}
