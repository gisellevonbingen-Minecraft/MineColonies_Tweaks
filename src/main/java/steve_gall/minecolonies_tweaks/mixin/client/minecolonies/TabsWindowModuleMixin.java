package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import org.spongepowered.asm.mixin.Mixin;

import com.ldtteam.blockui.controls.ButtonHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.minecolonies.core.client.gui.modules.TabsWindowModule;
import com.minecolonies.core.client.gui.modules.TabsWindowModule.TabImageSide;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

@Mixin(value = TabsWindowModule.class, remap = false)
public abstract class TabsWindowModuleMixin
{
	private final Object2IntMap<TabImageSide> minecolonies_tweaks$counts = new Object2IntOpenHashMap<>();

	@WrapMethod(method = "renderTabButton", remap = false)
	private void renderTabButton(int index, TabImageSide side, ResourceLocation icon, MutableComponent hoverText, ButtonHandler handler, Operation<Void> operation)
	{
		var count = this.minecolonies_tweaks$counts.getOrDefault(side, 0);

		if (side == TabImageSide.LEFT)
		{
			if (count > 7)
			{
				side = TabImageSide.RIGHT;
				index = this.minecolonies_tweaks$counts.getOrDefault(side, 0);
			}

		}

		this.minecolonies_tweaks$counts.put(side, index + 1);
		operation.call(index, side, icon, hoverText, handler);
	}

}
