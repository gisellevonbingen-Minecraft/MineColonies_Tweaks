package steve_gall.minecolonies_tweaks.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import steve_gall.minecolonies_tweaks.client.MineColoniesTweaksClient;
import steve_gall.minecolonies_tweaks.common.config.MineColoniesTweaksConfigCommon;
import steve_gall.minecolonies_tweaks.common.config.MineColoniesTweaksConfigServer;
import steve_gall.minecolonies_tweaks.common.mixin.AbstractEntityAICraftingAccessor;

@Mod(MineColoniesTweaks.MOD_ID)
public class MineColoniesTweaks
{
	public static final String MOD_ID = "minecolonies_tweaks";
	public static final Logger LOGGER = LogManager.getLogger();

	public MineColoniesTweaks()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MineColoniesTweaksConfigCommon.SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MineColoniesTweaksConfigServer.SPEC);

		IEventBus fml_bus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forge_bus = MinecraftForge.EVENT_BUS;
		forge_bus.addListener(this::onServerStarting);

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MineColoniesTweaksClient::new);
	}

	public static ResourceLocation rl(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}

	private void onServerStarting(ServerAboutToStartEvent e)
	{
		int pHittingTime = AbstractEntityAICraftingAccessor.getHittingTime();
		int nHittingTime = MineColoniesTweaksConfigCommon.INSTANCE.jobs.craftingHittingTime.get().intValue();
		AbstractEntityAICraftingAccessor.setHittingTime(nHittingTime);
		LOGGER.info("craftingHittingTime is changed: " + pHittingTime + " => " + nHittingTime);

		int pProgressMultiplier = AbstractEntityAICraftingAccessor.getProgressMultiplier();
		int nProgressMultiplier = MineColoniesTweaksConfigCommon.INSTANCE.jobs.craftingProgressMultiplier.get().intValue();
		AbstractEntityAICraftingAccessor.setProgressMultiplier(nProgressMultiplier);
		LOGGER.info("craftingProgressMultiplier is changed: " + pProgressMultiplier + " => " + nProgressMultiplier);
	}

}
