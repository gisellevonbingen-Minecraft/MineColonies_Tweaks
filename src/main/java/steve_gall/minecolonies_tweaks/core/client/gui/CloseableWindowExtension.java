package steve_gall.minecolonies_tweaks.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;

public interface CloseableWindowExtension
{
	void minecolonies_tweaks$setParent(Screen screen);

	Screen minecolonies_tweaks$getParent();

	default boolean returnToParent(boolean isEsc)
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

		Minecraft.getInstance().setScreen(parent);
		return true;
	}

}
