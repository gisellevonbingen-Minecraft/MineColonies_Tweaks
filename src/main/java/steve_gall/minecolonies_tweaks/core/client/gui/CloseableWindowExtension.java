package steve_gall.minecolonies_tweaks.core.client.gui;

import net.minecraft.client.gui.screens.Screen;

public interface CloseableWindowExtension
{
	void minecolonies_tweaks$setParent(Screen screen);

	Screen minecolonies_tweaks$getParent();
}
