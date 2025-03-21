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

@Mixin(value = WindowResearchTree.class, remap = false)
public abstract class WindowResearchTreeMixin extends Pane
{
	@Shadow(remap = false)
	private boolean hasMax;

	@Redirect(method = "<init>", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/ILocalResearchTree.branchFinishedHighestLevel", remap = false))
	private boolean init_branchFinishedHighestLevel(ILocalResearchTree tree, ResourceLocation branch)
	{
		if (this.mc.player.isCreative())
		{
			return false;
		}

		return tree.branchFinishedHighestLevel(branch);
	}

	@Redirect(method = {"getResearchButtonState", "onButtonClicked"}, remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/IGlobalResearch.getDepth", remap = false))
	private int getResearchButtonState_getDepth(IGlobalResearch research)
	{
		if (this.mc.player.isCreative())
		{
			return 0;
		}

		return research.getDepth();
	}

	@Redirect(method = "drawResearchItem", remap = false, at = @At(value = "INVOKE", target = "com/minecolonies/api/research/IGlobalResearch.hasOnlyChild", remap = false))
	private boolean drawResearchItem_hasOnlyChild(IGlobalResearch research)
	{
		if (this.mc.player.isCreative())
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
