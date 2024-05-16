package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.ldtteam.blockui.controls.ButtonImage;
import com.minecolonies.core.client.gui.AbstractModuleWindow;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;

import steve_gall.minecolonies_tweaks.core.client.gui.ButtonImageExtension;

@Mixin(value = AbstractModuleWindow.class, remap = false)
public abstract class AbstractModuleWindowMixin extends AbstractWindowSkeleton
{
	@Unique
	private boolean minecolonies_tweaks$right;

	public AbstractModuleWindowMixin(String resource)
	{
		super(resource);
	}

	@ModifyVariable(method = "<init>", remap = false, at = @At(value = "STORE"), ordinal = 0)
	private int init_offset(int offset)
	{
		if (offset + 26 >= this.height)
		{
			offset = 0;
			this.minecolonies_tweaks$right = true;
		}

		return offset;
	}

	@ModifyVariable(method = "<init>", remap = false, at = @At(value = "STORE"), ordinal = 0)
	private ButtonImage init_ButtonImage(ButtonImage button)
	{
		if (this.minecolonies_tweaks$right && button instanceof ButtonImageExtension extension)
		{
			extension.minecolonies_tweaks$setFlipX(true);
		}

		return button;
	}

	@ModifyConstant(method = "<init>", remap = false, constant = @Constant(intValue = -20, ordinal = 1))
	private int init_image_x(int x)
	{
		return this.minecolonies_tweaks$right ? (this.width + x + 8) : x;
	}

	@ModifyConstant(method = "<init>", remap = false, constant = @Constant(intValue = -15, ordinal = 1))
	private int init_icon_x(int x)
	{
		return this.minecolonies_tweaks$right ? (this.width + x + 10) : x;
	}

}
