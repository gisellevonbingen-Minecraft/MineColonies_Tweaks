package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.views.View;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.core.client.gui.AbstractWindowRequestTree;
import com.minecolonies.core.client.gui.WindowPostBox;
import com.minecolonies.core.client.gui.modules.MinimumStockModuleWindow;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;

import net.minecraft.core.BlockPos;
import steve_gall.minecolonies_tweaks.core.client.gui.ViewOverrideExtension;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

@Mixin(value = WindowPostBox.class, remap = false)
public abstract class WindowPostBoxMixin extends AbstractWindowRequestTree implements ViewOverrideExtension
{
	private static final String BUTTON_MIN_STACK = "min_stack";

	@Shadow(remap = false)
	private AbstractBuildingView buildingView;

	public WindowPostBoxMixin(BlockPos building, String pane, IColonyView colony)
	{
		super(building, pane, colony);
	}

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void init(AbstractBuildingView buildingView, CallbackInfo ci)
	{
		this.registerButton(BUTTON_MIN_STACK, this::minecolonies_tweaks$onMinStackClicked);
	}

	@Override
	public void minecolonies_tweaks$onParse(View view, PaneParams params)
	{
		Loader.createFromXMLFile(MineColoniesTweaks.rl("gui/windowpostbox.xml"), this);
	}

	private void minecolonies_tweaks$onMinStackClicked()
	{
		new MinimumStockModuleWindow(this.buildingView, this.buildingView.getModuleView(BuildingModules.MIN_STOCK)).open();
	}

}
