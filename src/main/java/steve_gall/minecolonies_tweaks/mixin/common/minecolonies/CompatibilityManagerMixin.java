package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.compatibility.CompatibilityManager;
import com.minecolonies.api.crafting.ItemStorage;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

@Mixin(value = CompatibilityManager.class, remap = false)
public abstract class CompatibilityManagerMixin
{
	@Shadow(remap = false)
	private List<ItemStorage> saplings;

	@Inject(method = "discoverAllItems", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void discoverAllItems(Level level, CallbackInfo ci)
	{
		this.saplings.add(new ItemStorage(new ItemStack(Items.CHORUS_FLOWER), false, true));
	}

}
