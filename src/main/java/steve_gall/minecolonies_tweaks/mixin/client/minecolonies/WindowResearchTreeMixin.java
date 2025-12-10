package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import static com.minecolonies.api.research.util.ResearchConstants.COLOR_TEXT_DARK;
import static com.minecolonies.api.research.util.ResearchConstants.COLOR_TEXT_UNFULFILLED;
import static com.minecolonies.api.research.util.ResearchConstants.GRADIENT_HEIGHT;
import static com.minecolonies.api.research.util.ResearchConstants.GRADIENT_WIDTH;
import static com.minecolonies.api.research.util.ResearchConstants.TEXT_X_OFFSET;
import static com.minecolonies.api.research.util.ResearchConstants.TEXT_Y_OFFSET;
import static com.minecolonies.api.util.constant.WindowConstants.BUTTON_HEIGHT;
import static com.minecolonies.api.util.constant.WindowConstants.BUTTON_LENGTH;
import static com.minecolonies.api.util.constant.WindowConstants.MEDIUM_SIZED_BUTTON_RES;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.ZoomDragView;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.research.ILocalResearchTree;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.client.gui.WindowResearchTree;
import com.minecolonies.core.client.gui.modules.building.UniversityModuleWindow;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraftforge.items.wrapper.InvWrapper;
import steve_gall.minecolonies_tweaks.api.common.building.BuildingPos;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;
import steve_gall.minecolonies_tweaks.core.common.inventory.InventoryUtils2;
import steve_gall.minecolonies_tweaks.core.common.network.message.ResearchCostRequestMessage;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCost;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCostResolver;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCostSelector;

@Mixin(value = WindowResearchTree.class, remap = false)
public abstract class WindowResearchTreeMixin extends AbstractWindowSkeleton
{
	@Unique
	private static final String minecolonies_tweaks$MISSING_COST_PREFIX = "missing_cost:";
	@Unique
	private static final String minecolonies_tweaks$REQUEST_PREFIX = "request:";

	@Shadow(remap = false)
	private UniversityModuleWindow last;
	@Shadow(remap = false)
	private IBuildingView building;
	@Shadow(remap = false)
	private ResourceLocation branch;
	@Shadow(remap = false)
	private ButtonImage undoButton;
	@Shadow(remap = false)
	private Text undoText;

	public WindowResearchTreeMixin(ResourceLocation resource)
	{
		super(resource);
	}

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

	@WrapOperation(method = {"generateResearchTooltips", "getResearchButtonState", "onButtonClicked"}, remap = false, at = @At(value = "NEW", target = "net/minecraftforge/items/wrapper/InvWrapper", remap = false))
	private InvWrapper newInvWrapper(Container inv, Operation<InvWrapper> operation)
	{
		var original = operation.call(inv);
		var level = this.building.getColony().getWorld();

		if (level != null)
		{
			return InventoryUtils2.wrapWithBuilding(original, level, this.building.getPosition());
		}

		return original;
	}

	@Inject(method = "drawResearchBoxes", remap = false, at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void drawResearchBoxes(ZoomDragView view, int offsetX, int offsetY, IGlobalResearch research, @Coerce Object state, int progress, CallbackInfo ci, ButtonImage nameBar)
	{
		if (state.toString().equals("MISSING_COST"))
		{
			nameBar.setID(minecolonies_tweaks$MISSING_COST_PREFIX + research.getId().toString());
		}

	}

	@Inject(method = "onButtonClicked", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void onButtonClicked(Button button, CallbackInfo ci)
	{
		var buttonId = button.getID();

		if (buttonId.startsWith(minecolonies_tweaks$MISSING_COST_PREFIX))
		{
			if (ResearchCostResolver.hasResolver(this.building))
			{
				var researchId = new ResourceLocation(buttonId.substring(minecolonies_tweaks$MISSING_COST_PREFIX.length()));
				var research = IGlobalResearchTree.getInstance().getResearch(this.branch, researchId);
				this.minecolonies_tweaks$drawRequestButton(button, research);
			}

		}
		else if (buttonId.startsWith(minecolonies_tweaks$REQUEST_PREFIX))
		{
			this.close();

			var branchId = this.branch;
			var researchId = new ResourceLocation(buttonId.substring(minecolonies_tweaks$REQUEST_PREFIX.length()));
			var player = this.mc.player;

			if (ResearchCost.isRequested(this.building, branchId, researchId))
			{
				player.sendSystemMessage(Component.translatable("minecolonies_tweaks.gui.already_requested").withStyle(ChatFormatting.GRAY));
				SoundUtils.playErrorSound(player, this.building.getPosition());
			}
			else
			{
				var research = IGlobalResearchTree.getInstance().getResearch(branchId, researchId);
				ResearchCostSelector.open(this.last, research, items ->
				{
					MineColoniesTweaks.network().sendToServer(new ResearchCostRequestMessage(new BuildingPos(this.building), research.getBranch(), research.getId(), items));
				});
			}

		}

	}

	@Unique
	private void minecolonies_tweaks$drawRequestButton(Button parent, IGlobalResearch research)
	{
		var undoButton = this.undoButton;
		undoButton.setImage(new ResourceLocation(Constants.MOD_ID, MEDIUM_SIZED_BUTTON_RES), false);
		undoButton.setSize(BUTTON_LENGTH, BUTTON_HEIGHT);
		undoButton.setPosition(parent.getX() + (GRADIENT_WIDTH - BUTTON_LENGTH) / 2, parent.getY() + TEXT_Y_OFFSET + (GRADIENT_HEIGHT - BUTTON_HEIGHT) / 2);
		undoButton.setID(minecolonies_tweaks$REQUEST_PREFIX + research.getId().toString());
		parent.getParent().addChild(undoButton);

		var undoText = this.undoText;
		undoText.setSize(BUTTON_LENGTH, BUTTON_HEIGHT);
		undoText.setPosition(parent.getX() + TEXT_X_OFFSET + (GRADIENT_WIDTH - BUTTON_LENGTH) / 2, parent.getY() + TEXT_Y_OFFSET + (GRADIENT_HEIGHT - BUTTON_HEIGHT) / 2);
		undoText.setColors(COLOR_TEXT_DARK);
		undoText.setText(Component.translatable("minecolonies_tweaks.gui.request_research_cost"));
		undoText.disable();
		parent.getParent().addChild(undoText);

		var tooltip = PaneBuilders.tooltipBuilder().hoverPane(undoButton);
		tooltip.append(Component.translatable("minecolonies_tweaks.gui.request_research_cost")).bold();
		tooltip.paragraphBreak().append(Component.translatable("minecolonies_tweaks.gui.request_research_cost.desc"));

		if (ResearchCost.isRequested(this.building, research.getBranch(), research.getId()))
		{
			tooltip.paragraphBreak().append(Component.translatable("minecolonies_tweaks.gui.already_requested")).color(COLOR_TEXT_UNFULFILLED);
		}

		for (var cost : research.getCostList())
		{
			var text = Component.literal(" - ").append(Component.translatable("com.minecolonies.coremod.research.limit.requirement", cost.getCount(), cost.getTranslatedName()));
			tooltip.paragraphBreak().append(text);
		}

		tooltip.build();
	}

}
