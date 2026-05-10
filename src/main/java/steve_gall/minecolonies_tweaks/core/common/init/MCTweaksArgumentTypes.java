package steve_gall.minecolonies_tweaks.core.common.init;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.command.DiseaseArgumentType;

public class MCTweaksArgumentTypes
{
	public static final DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTER = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, MineColoniesTweaks.MOD_ID);

	public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<DiseaseArgumentType>> DISEASE = REGISTER.register("disease", () -> ArgumentTypeInfos.registerByClass(DiseaseArgumentType.class, SingletonArgumentInfo.contextFree(DiseaseArgumentType::instance)));

	private MCTweaksArgumentTypes()
	{

	}

}
