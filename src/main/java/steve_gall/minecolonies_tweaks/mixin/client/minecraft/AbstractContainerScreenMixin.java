package steve_gall.minecolonies_tweaks.mixin.client.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import steve_gall.minecolonies_tweaks.core.client.gui.AbstractContainerScreenExtension;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableContainerScreenExtension;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;

@Mixin(value = AbstractContainerScreen.class, remap = true)
public abstract class AbstractContainerScreenMixin extends Screen implements AbstractContainerScreenExtension
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

	@Override
	public void minecolonies_tweaks$onInitPost()
	{
		if (this instanceof CloseableContainerScreenExtension closeable)
		{
			closeable.minecolonies_tweaks$onInit(this.leftPos, this.topPos, this.imageWidth, this.imageHeight, this::minecolonies_tweaks$addCloseButton);
		}

	}

	@Unique
	private boolean minecolonies_tweaks$addCloseButton(int x, int y, int width, int height)
	{
		if (MineColoniesTweaksConfigClient.INSTANCE.addReturnButton.get().booleanValue())
		{
			var closeButton = Button.builder(Component.literal("X"), this::minecolonies_tweaks$onClosePress).bounds(x, y, width, height).build();
			this.addRenderableWidget(closeButton);
			return true;
		}
		else
		{
			return false;
		}

	}

	@Unique
	private void minecolonies_tweaks$onClosePress(Button button)
	{
		if (this instanceof CloseableContainerScreenExtension closeable)
		{
			this.onClose();
			closeable.minecolonies_tweaks$showParent(false);
		}

	}

}
