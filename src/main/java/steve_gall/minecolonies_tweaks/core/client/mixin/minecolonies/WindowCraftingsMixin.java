package steve_gall.minecolonies_tweaks.core.client.mixin.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.core.client.gui.containers.WindowBrewingstandCrafting;
import com.minecolonies.core.client.gui.containers.WindowCrafting;
import com.minecolonies.core.client.gui.containers.WindowFurnaceCrafting;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import steve_gall.minecolonies_tweaks.core.client.gui.WindowCraftingExtension;

@Mixin(value = {WindowCrafting.class, WindowFurnaceCrafting.class, WindowBrewingstandCrafting.class}, remap = false)
public abstract class WindowCraftingsMixin extends AbstractContainerScreen<AbstractContainerMenu> implements WindowCraftingExtension
{
	@Shadow(remap = false)
	private static int BUTTON_X_OFFSET;
	@Shadow(remap = false)
	private static int BUTTON_Y_POS;
	@Shadow(remap = false)
	private static int BUTTON_WIDTH;
	@Shadow(remap = false)
	private static int BUTTON_HEIGHT;

	@Unique
	private Screen minecolonies_tweaks$parent;

	public WindowCraftingsMixin(AbstractContainerMenu p_97741_, Inventory p_97742_, Component p_97743_)
	{
		super(p_97741_, p_97742_, p_97743_);
	}

	@Inject(method = "init", remap = true, at = @At(value = "TAIL"))
	private void init(CallbackInfo ci)
	{
		var closeButton = Button.builder(Component.literal("X"), this::onClosePress).bounds(this.leftPos + BUTTON_X_OFFSET + BUTTON_WIDTH + 5, this.topPos + BUTTON_Y_POS, BUTTON_HEIGHT, BUTTON_HEIGHT).build();
		this.addRenderableWidget(closeButton);
	}

	private void onClosePress(Button button)
	{
		this.onClose();

		var parent = this.minecolonies_tweaks$getParent();

		if (parent != null)
		{
			this.minecraft.setScreen(parent);
		}

	}

	@Override
	public void minecolonies_tweaks$setParent(Screen screen)
	{
		this.minecolonies_tweaks$parent = screen;
	}

	@Override
	public Screen minecolonies_tweaks$getParent()
	{
		return this.minecolonies_tweaks$parent;
	}

}
