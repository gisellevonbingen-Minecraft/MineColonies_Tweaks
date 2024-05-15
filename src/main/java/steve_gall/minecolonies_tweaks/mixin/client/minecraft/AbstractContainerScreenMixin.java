package steve_gall.minecolonies_tweaks.mixin.client.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableWindowExtension;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;

@Mixin(value = AbstractContainerScreen.class, remap = true)
public abstract class AbstractContainerScreenMixin extends Screen
{
	@Shadow(remap = true)
	protected int leftPos;
	@Shadow(remap = true)
	protected int topPos;
	@Shadow(remap = true)
	protected int imageWidth;
	@Shadow(remap = true)
	protected int imageHeight;

	protected AbstractContainerScreenMixin(Component p_96550_)
	{
		super(p_96550_);
	}

	@Inject(method = "init", remap = true, at = @At(value = "TAIL"))
	protected void init(CallbackInfo ci)
	{

	}

	protected boolean addCloseButton(int x, int y, int width, int height)
	{
		if (MineColoniesTweaksConfigClient.INSTANCE.addReturnButton.get().booleanValue())
		{
			var closeButton = new Button(x, y, width, height, Component.literal("X"), this::onClosePress);
			this.addRenderableWidget(closeButton);
			return true;
		}
		else
		{
			return false;
		}

	}

	private void onClosePress(Button button)
	{
		this.onClose();
		this.returnToParent(false);
	}

	private void returnToParent(boolean isEsc)
	{
		if (this instanceof CloseableWindowExtension self)
		{
			self.returnToParent(isEsc);
		}

	}

	@Inject(method = "onClose", remap = true, at = @At(value = "TAIL"))
	private void onClose(CallbackInfo ci)
	{
		this.returnToParent(true);
	}

}
