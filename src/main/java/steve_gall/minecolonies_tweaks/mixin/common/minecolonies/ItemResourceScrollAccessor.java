package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.minecolonies.core.items.ItemResourceScroll;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

@Mixin(value = ItemResourceScroll.class, remap = false)
public interface ItemResourceScrollAccessor
{
	@Invoker(value = "openWindow", remap = false)
	static void invokeOpenWindow(CompoundTag compound, Player player)
	{

	}

}
