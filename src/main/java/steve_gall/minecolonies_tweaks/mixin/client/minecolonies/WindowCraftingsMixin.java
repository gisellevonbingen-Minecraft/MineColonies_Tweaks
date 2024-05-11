package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.core.client.gui.containers.WindowBrewingstandCrafting;
import com.minecolonies.core.client.gui.containers.WindowCrafting;
import com.minecolonies.core.client.gui.containers.WindowFurnaceCrafting;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import steve_gall.minecolonies_tweaks.core.client.gui.CloseableWindowExtension;
import steve_gall.minecolonies_tweaks.mixin.client.minecraft.AbstractContainerScreenMixin;

@Mixin(value = {WindowCrafting.class, WindowFurnaceCrafting.class, WindowBrewingstandCrafting.class}, remap = false)
public abstract class WindowCraftingsMixin extends AbstractContainerScreenMixin implements CloseableWindowExtension
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

	protected WindowCraftingsMixin(Component p_96550_)
	{
		super(p_96550_);
	}

	@Override
	protected void init(CallbackInfo ci)
	{
		super.init(ci);

		this.addCloseButton(this.leftPos + BUTTON_X_OFFSET + BUTTON_WIDTH + 5, this.topPos + BUTTON_Y_POS, BUTTON_HEIGHT, BUTTON_HEIGHT);
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
