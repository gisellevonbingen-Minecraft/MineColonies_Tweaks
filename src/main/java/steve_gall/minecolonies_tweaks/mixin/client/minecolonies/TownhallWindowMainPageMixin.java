package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.views.View;
import com.minecolonies.core.client.gui.townhall.AbstractWindowTownHall;
import com.minecolonies.core.client.gui.townhall.WindowMainPage;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingTownHall;

import steve_gall.minecolonies_tweaks.core.client.gui.BatchRepairBuildingsWindow;
import steve_gall.minecolonies_tweaks.core.client.gui.BatchUpgradeBuildingsWindow;
import steve_gall.minecolonies_tweaks.core.client.gui.ViewOverrideExtension;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

@Mixin(value = WindowMainPage.class, remap = false)
public abstract class TownhallWindowMainPageMixin extends AbstractWindowTownHall implements ViewOverrideExtension
{
	public TownhallWindowMainPageMixin(BuildingTownHall.View townHall, String page)
	{
		super(townHall, page);
	}

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"))
	private void init(BuildingTownHall.View building, CallbackInfo ci)
	{
		this.registerButton("batchRepair", this::onBatchRepairClick);
		this.registerButton("batchUpgrade", this::onBatchUpgradeClick);
	}

	private void onBatchRepairClick()
	{
		new BatchRepairBuildingsWindow(this, this.buildingView.getColony()).open();
	}

	private void onBatchUpgradeClick()
	{
		new BatchUpgradeBuildingsWindow(this, this.buildingView.getColony()).open();
	}

	@Override
	public void minecolonies_tweaks$onParse(View view, PaneParams params)
	{
		Loader.createFromXMLFile(MineColoniesTweaks.rl("gui/townhall/layoutactions.xml"), this);
	}

}
