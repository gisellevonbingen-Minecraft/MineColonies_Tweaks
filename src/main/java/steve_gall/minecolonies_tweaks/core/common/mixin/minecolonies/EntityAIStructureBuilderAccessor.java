package steve_gall.minecolonies_tweaks.core.common.mixin.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.ai.workers.builder.EntityAIStructureBuilder;

@Mixin(value = EntityAIStructureBuilder.class, remap = false)
public interface EntityAIStructureBuilderAccessor
{
	@Accessor(value = "SPEED_BUFF_0")
	static double getSpeedBuff0()
	{
		throw new AssertionError();
	}

}
