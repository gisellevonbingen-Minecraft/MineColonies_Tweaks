package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.ldtteam.blockui.Pane;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.research.ILocalResearchTree;
import com.minecolonies.core.client.gui.WindowResearchTree;

import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = WindowResearchTree.class, remap = false)
public abstract class WindowResearchTreeMixin extends Pane
{
	@Shadow(remap = false)
	private boolean hasMax;

	@Redirect(method = "<init>", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/ILocalResearchTree.branchFinishedHighestLevel", remap = false))
	private boolean init_branchFinishedHighestLevel(ILocalResearchTree tree, ResourceLocation branch)
	{
		if (this.mc.player.isCreative() || MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get())
		{
			return false;
		}

		return tree.branchFinishedHighestLevel(branch);
	}

	@Redirect(method = "getResearchButtonState", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/IGlobalResearch.getDepth", remap = false))
	private int getResearchButtonState_getDepth(IGlobalResearch research)
	{
		if (this.mc.player.isCreative())
		{
			return 0;
		}

		return research.getDepth();
	}

	@Redirect(method = {"onButtonClicked", "generateResearchTooltips"}, remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/IGlobalResearch.getDepth", remap = false))
	private int onButtonClicked_getDepth(IGlobalResearch research)
	{
		if (this.mc.player.isCreative() || MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get())
		{
			return 0;
		}

		return research.getDepth();
	}

	@Redirect(method = "getResearchButtonState", remap = false, at = @At(value = "FIELD", target = "hasMax:Z", remap = false))
	private boolean getResearchButtonState_hasMax(WindowResearchTree self)
	{
		if (MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get())
		{
			return false;
		}

		return this.hasMax;
	}

	@Redirect(method = {"drawResearchItem", "drawArrows"}, remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/IGlobalResearch.hasOnlyChild", remap = false))
	private boolean drawResearchItem_hasOnlyChild(IGlobalResearch research)
	{
		if (this.mc.player.isCreative() || MCTweaksConfigServer.INSTANCE.researches.ignoreConstraints.get())
		{
			return false;
		}

		return research.hasOnlyChild();
	}

	@Redirect(method = "drawTreeBackground", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/colony/buildings/views/IBuildingView.getBuildingLevel", remap = false))
	private int drawTreeBackground_getBuildingLevel(IBuildingView building)
	{
		if (this.mc.player.isCreative())
		{
			return building.getBuildingMaxLevel();
		}

		return building.getBuildingLevel();
	}

}
