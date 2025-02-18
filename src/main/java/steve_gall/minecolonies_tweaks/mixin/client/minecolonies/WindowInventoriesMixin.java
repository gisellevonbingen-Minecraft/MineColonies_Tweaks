package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.minecolonies.core.client.gui.containers.WindowBuildingInventory;
import com.minecolonies.core.client.gui.containers.WindowCitizenInventory;

import net.minecraft.client.gui.screens.Screen;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableContainerScreenExtension;

@Mixin(value = {WindowCitizenInventory.class, WindowBuildingInventory.class}, remap = false)
public abstract class WindowInventoriesMixin implements CloseableContainerScreenExtension
{
	@Unique
	private Screen minecolonies_tweaks$parent;

	@Override
	public void minecolonies_tweaks$onInit(int leftPos, int topPos, int imageWidth, int imageHeight, addCloseButton addCloseButton)
	{
		addCloseButton.invoke(leftPos + imageWidth - 20, topPos - 5, 20, 20);
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
