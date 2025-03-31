package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.minecolonies.core.client.gui.WindowAssignCitizen;
import com.minecolonies.core.client.gui.WindowBuildBuilding;
import com.minecolonies.core.client.gui.WindowClipBoard;
import com.minecolonies.core.client.gui.WindowHireWorker;
import com.minecolonies.core.client.gui.WindowHutAllInventory;
import com.minecolonies.core.client.gui.WindowResourceList;
import com.minecolonies.core.client.gui.map.WindowColonyMap;

import net.minecraft.client.gui.screens.Screen;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableWindowExtension;

@Mixin(value = {WindowBuildBuilding.class, WindowHireWorker.class, WindowHutAllInventory.class, WindowAssignCitizen.class, WindowResourceList.class, WindowClipBoard.class, WindowColonyMap.class}, remap = false)
public abstract class AbstractWindowSkeletonsMixin implements CloseableWindowExtension
{
	@Unique
	private Screen minecolonies_tweaks$parent;

	@Override
	public Screen minecolonies_tweaks$getParent()
	{
		return this.minecolonies_tweaks$parent;
	}

	@Override
	public void minecolonies_tweaks$setParent(Screen screen)
	{
		this.minecolonies_tweaks$parent = screen;
	}

}
