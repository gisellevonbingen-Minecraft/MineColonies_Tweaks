package steve_gall.minecolonies_tweaks.core.common.init;

import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;

import net.minecraftforge.registries.DeferredRegister;
import steve_gall.minecolonies_tweaks.api.registries.DeferredRegisterHelper;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class ModEquipmentTypes
{
	public static final DeferredRegister<EquipmentTypeEntry> REGISTER = DeferredRegisterHelper.equipmentTypes(MineColoniesTweaks.MOD_ID);

}
