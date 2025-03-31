package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.minecolonies.core.items.ItemColonyMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(value = ItemColonyMap.class, remap = false)
public interface ItemColonyMapAccessor
{
	@Invoker(value = "openWindow", remap = false)
	static void invokeOpenWindow(CompoundTag compound, Level level, Player player)
	{

	}

}
