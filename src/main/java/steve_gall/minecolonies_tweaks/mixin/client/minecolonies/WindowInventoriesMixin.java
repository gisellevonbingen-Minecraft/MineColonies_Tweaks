package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.core.client.gui.containers.WindowBuildingInventory;
import com.minecolonies.core.client.gui.containers.WindowCitizenInventory;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableWindowExtension;
import steve_gall.minecolonies_tweaks.mixin.client.minecraft.AbstractContainerScreenMixin;

@Mixin(value = {WindowCitizenInventory.class, WindowBuildingInventory.class}, remap = false)
public abstract class WindowInventoriesMixin extends AbstractContainerScreenMixin implements CloseableWindowExtension
{
	@Unique
	private Screen minecolonies_tweaks$parent;

	protected WindowInventoriesMixin(Component p_96550_)
	{
		super(p_96550_);
	}

	@Override
	protected void init(CallbackInfo ci)
	{
		super.init(ci);

		this.addCloseButton(this.leftPos + this.imageWidth - 20, this.topPos - 5, 20, 20);
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
