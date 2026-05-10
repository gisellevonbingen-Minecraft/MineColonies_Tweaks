package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenDiseaseHandler;

@Mixin(value = CitizenDiseaseHandler.class, remap = false)
public interface CitizenDiseaseHandlerAccessor
{
	@Accessor(value = "immunityTicks", remap = false)
	void setImmunityTicks(int immunityTicks);
}
