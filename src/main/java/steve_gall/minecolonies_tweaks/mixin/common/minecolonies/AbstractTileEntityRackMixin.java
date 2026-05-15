package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.minecolonies.api.tileentities.AbstractTileEntityRack;

import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

@Mixin(value = AbstractTileEntityRack.class, remap = false)
public abstract class AbstractTileEntityRackMixin implements Clearable
{
	@Shadow(remap = false)
	private ItemStackHandler inventory;

	@Override
	public void clearContent()
	{
		var inventory = this.inventory;

		for (var i = 0; i < inventory.getSlots(); i++)
		{
			inventory.setStackInSlot(i, ItemStack.EMPTY);
		}

	}

}
