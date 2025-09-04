package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.minecolonies.core.items.ItemQuestLog;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(value = ItemQuestLog.class, remap = false)
public interface ItemQuestLogAccessor
{
	@Invoker(value = "openWindow", remap = false)
	static void invokeOpenWindow(ItemStack stack, Level level, Player player)
	{

	}

}
