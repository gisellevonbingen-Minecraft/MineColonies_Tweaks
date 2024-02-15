package steve_gall.minecolonies_tweaks.common.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import com.minecolonies.core.entity.ai.basic.AbstractEntityAICrafting;

@Mixin(value = AbstractEntityAICrafting.class, remap = false)
public abstract class AbstractEntityAICraftingMixin
{
	@Final
	@Mutable
	@Shadow
	private static int PROGRESS_MULTIPLIER;

	@Final
	@Mutable
	@Shadow
	private static int HITTING_TIME;
}
