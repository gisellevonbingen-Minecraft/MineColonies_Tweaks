package steve_gall.minecolonies_tweaks.core.common.init;

import static com.minecolonies.api.util.constant.Constants.MOD_ID;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import steve_gall.minecolonies_tweaks.core.common.command.DiseaseArgumentType;

public class MCTweaksArgumentTypes
{
	public static final DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTER = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, MOD_ID);

	public static final RegistryObject<SingletonArgumentInfo<DiseaseArgumentType>> DISEASE = REGISTER.register("disease", () -> ArgumentTypeInfos.registerByClass(DiseaseArgumentType.class, SingletonArgumentInfo.contextFree(DiseaseArgumentType::instance)));

	private MCTweaksArgumentTypes()
	{

	}

}
