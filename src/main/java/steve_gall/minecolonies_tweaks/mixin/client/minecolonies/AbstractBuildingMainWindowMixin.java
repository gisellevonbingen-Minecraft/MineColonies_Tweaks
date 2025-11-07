package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.controls.Text;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.client.gui.AbstractBuildingMainWindow;
import com.minecolonies.core.client.gui.AbstractBuildingWindow;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingWareHouse;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.building.BuildingEmptySlotCounter;

@Mixin(value = AbstractBuildingMainWindow.class, remap = false)
public abstract class AbstractBuildingMainWindowMixin<B extends IBuildingView> extends AbstractBuildingWindow<B>
{
	private static final String TEXT_EMPTY_SLOTS = "empty_slots";

	@Unique
	private BuildingEmptySlotCounter minecolonies_tweaks$slotCounter;

	public AbstractBuildingMainWindowMixin(B buildingView, ResourceLocation res)
	{
		super(buildingView, res);
	}

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void init(B building, ResourceLocation resource, CallbackInfo ci)
	{
		if (building instanceof BuildingWareHouse.View)
		{
			Loader.createFromXMLFile(MineColoniesTweaks.rl("gui/layouthuts/layoutwarehouse.xml"), this);
		}

	}

	@Inject(method = "onUpdate", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void onUpdate(CallbackInfo ci)
	{
		if (this.buildingView instanceof BuildingWareHouse.View)
		{
			if (this.minecolonies_tweaks$slotCounter == null)
			{
				this.minecolonies_tweaks$slotCounter = new BuildingEmptySlotCounter(this.buildingView);
			}

			if (this.minecolonies_tweaks$slotCounter.update())
			{
				this.updateText();
			}

		}

	}

	private void updateText()
	{
		var slotsText = this.findPaneOfTypeByID(TEXT_EMPTY_SLOTS, Text.class);
		var text = Component.empty();
		text.append(this.minecolonies_tweaks$slotCounter.getSlotsText());
		text.append(" (" + this.minecolonies_tweaks$slotCounter.getPercentText() + ")");
		slotsText.setText(text);
	}

}
