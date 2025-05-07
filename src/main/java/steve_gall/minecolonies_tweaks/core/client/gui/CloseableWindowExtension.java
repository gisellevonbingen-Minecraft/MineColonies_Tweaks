package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.Optional;

import com.ldtteam.blockui.BOScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigClient;

public interface CloseableWindowExtension
{
	public static Optional<CloseableWindowExtension> find(Object object)
	{
		if (object instanceof BOScreen boScreen && boScreen.getWindow() instanceof CloseableWindowExtension extension)
		{
			return Optional.of(extension);
		}
		else if (object instanceof CloseableWindowExtension extension)
		{
			return Optional.of(extension);
		}

		return Optional.empty();
	}

	void minecolonies_tweaks$setParent(Screen screen);

	Screen minecolonies_tweaks$getParent();

	default boolean minecolonies_tweaks$showParent()
	{
		return this.minecolonies_tweaks$showParent(true);
	}

	default boolean minecolonies_tweaks$showParent(boolean isEsc)
	{
		if (isEsc && !MCTweaksConfigClient.INSTANCE.escToReturn.get().booleanValue())
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
