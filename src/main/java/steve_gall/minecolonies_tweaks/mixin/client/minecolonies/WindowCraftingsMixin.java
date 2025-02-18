package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.minecolonies.core.client.gui.containers.WindowBrewingstandCrafting;
import com.minecolonies.core.client.gui.containers.WindowCrafting;
import com.minecolonies.core.client.gui.containers.WindowFurnaceCrafting;

import net.minecraft.client.gui.screens.Screen;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableContainerScreenExtension;

@Mixin(value = {WindowCrafting.class, WindowFurnaceCrafting.class, WindowBrewingstandCrafting.class}, remap = false)
public abstract class WindowCraftingsMixin implements CloseableContainerScreenExtension
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

	@Override
	public void minecolonies_tweaks$onInit(int leftPos, int topPos, int imageWidth, int imageHeight, addCloseButton addCloseButton)
	{
		addCloseButton.invoke(leftPos + BUTTON_X_OFFSET + BUTTON_WIDTH + 5, topPos + BUTTON_Y_POS, BUTTON_HEIGHT, BUTTON_HEIGHT);
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
