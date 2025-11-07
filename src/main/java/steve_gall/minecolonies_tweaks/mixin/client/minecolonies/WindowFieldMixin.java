package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.views.View;
import com.minecolonies.api.tileentities.AbstractTileEntityScarecrow;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.client.gui.containers.WindowField;
import com.minecolonies.core.colony.buildingextensions.FarmField;
import com.minecolonies.core.network.messages.server.colony.building.fields.FarmFieldPlotResizeMessage;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;
import steve_gall.minecolonies_tweaks.core.client.gui.ViewOverrideExtension;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

@Mixin(value = WindowField.class, remap = false)
public abstract class WindowFieldMixin extends AbstractWindowSkeleton implements ViewOverrideExtension
{
	@Shadow(remap = false)
	private static String DIRECTIONAL_BUTTON_ID_PREFIX;

	@Unique
	private static final String minecolonies_tweaks$DIRECTIONAL_UP_BUTTON_ID_PREFIX = DIRECTIONAL_BUTTON_ID_PREFIX + "up-";
	@Unique
	private static final String minecolonies_tweaks$DIRECTIONAL_DOWN_BUTTON_ID_PREFIX = DIRECTIONAL_BUTTON_ID_PREFIX + "down-";
	@Unique
	private static final int minecolonies_tweaks$SHIFT_MULTIPLIER = 5;

	@Shadow(remap = false)
	private FarmField farmField;
	@NotNull
	@Shadow(remap = false)
	private AbstractTileEntityScarecrow tileEntityScarecrow;

	public WindowFieldMixin(ResourceLocation resource)
	{
		super(resource);
	}

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"))
	private void init(@NotNull AbstractTileEntityScarecrow tileEntityScarecrow, CallbackInfo ci)
	{
		for (Direction dir : Direction.Plane.HORIZONTAL)
		{
			this.registerButton(minecolonies_tweaks$DIRECTIONAL_UP_BUTTON_ID_PREFIX + dir.getName(), this::minecolonies_tweaks$onDirectionalUpButtonClick);
			this.registerButton(minecolonies_tweaks$DIRECTIONAL_DOWN_BUTTON_ID_PREFIX + dir.getName(), this::minecolonies_tweaks$onDirectionalDownButtonClick);
		}

		this.registerButton(minecolonies_tweaks$DIRECTIONAL_UP_BUTTON_ID_PREFIX + "all", this::minecolonies_tweaks$onAllUpButtonClick);
		this.registerButton(minecolonies_tweaks$DIRECTIONAL_DOWN_BUTTON_ID_PREFIX + "all", this::minecolonies_tweaks$onAllDownButtonClick);
		this.updateAll();
	}

	@Override
	public void minecolonies_tweaks$onParse(View view, PaneParams params)
	{
		Loader.createFromXMLFile(MineColoniesTweaks.rl("gui/windowfield.xml"), this);
	}

	@Unique
	private void minecolonies_tweaks$changRadius(Button button, String prefix, int delta)
	{
		if (this.farmField == null || !button.isEnabled())
		{
			return;
		}

		var directionName = button.getID().replace(prefix, "");
		var direction = Direction.Plane.HORIZONTAL.stream().filter(f -> f.getName().equals(directionName)).findFirst();

		if (direction.isPresent())
		{
			this.minecolonies_tweaks$changRadius(direction.get(), delta);
		}

	}

	private void minecolonies_tweaks$changRadius(Direction direction, int delta)
	{
		if (Screen.hasShiftDown())
		{
			delta *= minecolonies_tweaks$SHIFT_MULTIPLIER;
		}

		var newRadius = Mth.clamp(this.farmField.getRadius(direction) + delta, 1, this.farmField.getMaxRadius());
		this.farmField.setRadius(direction, newRadius);

		var arrowButton = this.findPaneOfTypeByID(DIRECTIONAL_BUTTON_ID_PREFIX + direction.getName(), Button.class);
		arrowButton.setText(Component.literal(String.valueOf(newRadius)));

		PacketDistributor.sendToServer(new FarmFieldPlotResizeMessage(this.tileEntityScarecrow.getCurrentColony(), newRadius, direction, farmField.getPosition()));
	}

	@Unique
	private void minecolonies_tweaks$onDirectionalUpButtonClick(Button button)
	{
		this.minecolonies_tweaks$changRadius(button, minecolonies_tweaks$DIRECTIONAL_UP_BUTTON_ID_PREFIX, +1);
	}

	@Unique
	private void minecolonies_tweaks$onDirectionalDownButtonClick(Button button)
	{
		this.minecolonies_tweaks$changRadius(button, minecolonies_tweaks$DIRECTIONAL_DOWN_BUTTON_ID_PREFIX, -1);
	}

	@Unique
	private void minecolonies_tweaks$onAllUpButtonClick(Button button)
	{
		Direction.Plane.HORIZONTAL.forEach(dir -> this.minecolonies_tweaks$changRadius(dir, +1));
	}

	@Unique
	private void minecolonies_tweaks$onAllDownButtonClick(Button button)
	{
		Direction.Plane.HORIZONTAL.forEach(dir -> this.minecolonies_tweaks$changRadius(dir, -1));
	}

	@Shadow(remap = false)
	public abstract void updateAll();

	@Inject(method = "updateButtons", remap = false, at = @At(value = "TAIL"), cancellable = true)
	private void updateButtons(CallbackInfo ci)
	{
		for (Direction dir : Direction.Plane.HORIZONTAL)
		{
			this.minecolonies_tweaks$updateButton(minecolonies_tweaks$DIRECTIONAL_UP_BUTTON_ID_PREFIX + dir.getName());
			this.minecolonies_tweaks$updateButton(minecolonies_tweaks$DIRECTIONAL_DOWN_BUTTON_ID_PREFIX + dir.getName());
		}

		this.minecolonies_tweaks$updateButton(minecolonies_tweaks$DIRECTIONAL_UP_BUTTON_ID_PREFIX + "all");
		this.minecolonies_tweaks$updateButton(minecolonies_tweaks$DIRECTIONAL_DOWN_BUTTON_ID_PREFIX + "all");
	}

	@Unique
	private void minecolonies_tweaks$updateButton(String name)
	{
		var button = this.findPaneOfTypeByID(name, Button.class);

		if (button == null)
		{
			return;
		}

		button.setEnabled(!Objects.isNull(this.farmField));
	}

}
