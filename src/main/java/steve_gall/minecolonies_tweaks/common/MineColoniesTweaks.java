package steve_gall.minecolonies_tweaks.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import steve_gall.minecolonies_tweaks.client.MineColoniesTweaksClient;
import steve_gall.minecolonies_tweaks.common.config.MineColoniesTweaksConfigCommon;

@Mod(MineColoniesTweaks.MOD_ID)
public class MineColoniesTweaks
{
	public static final String MOD_ID = "minecolonies_tweaks";
	public static final Logger LOGGER = LogManager.getLogger();

	public MineColoniesTweaks()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MineColoniesTweaksConfigCommon.SPEC);

		IEventBus fml_bus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forge_bus = MinecraftForge.EVENT_BUS;

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MineColoniesTweaksClient::new);
	}

	public static ResourceLocation rl(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}

}
