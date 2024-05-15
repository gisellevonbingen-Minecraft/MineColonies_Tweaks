package steve_gall.minecolonies_tweaks.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;

public interface CloseableWindowExtension
{
	void minecolonies_tweaks$setParent(Screen screen);

	Screen minecolonies_tweaks$getParent();

	default boolean minecolonies_tweaks$returnOrClose()
	{
		if (this instanceof Screen screen)
		{
			var closed = false;

			if (screen instanceof AbstractContainerScreen<?> containerScreen)
			{
				containerScreen.onClose();
				closed = true;
			}

			if (!this.minecolonies_tweaks$showParent(false))
			{
				if (!closed)
				{
					screen.onClose();
				}

			}

			return true;
		}
		else
		{
			return false;
		}

	}

	default boolean minecolonies_tweaks$showParent()
	{
		return this.minecolonies_tweaks$showParent(true);
	}

	default boolean minecolonies_tweaks$showParent(boolean isEsc)
	{
		if (isEsc && !MineColoniesTweaksConfigClient.INSTANCE.escToReturn.get().booleanValue())
		{
			return false;
		}

		var parent = this.minecolonies_tweaks$getParent();

		if (parent == null)
		{
			return false;
		}

		var minecraft = Minecraft.getInstance();

		if (minecraft.screen != parent)
		{
			minecraft.setScreen(parent);
		}

		return true;
	}

}
