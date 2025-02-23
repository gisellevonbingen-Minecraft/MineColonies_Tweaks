package steve_gall.minecolonies_tweaks.core.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.manager.RequestMappingHandler;
import com.minecolonies.api.items.ModItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableDeliverable;
import steve_gall.minecolonies_tweaks.core.client.MineColoniesTweaksClient;
import steve_gall.minecolonies_tweaks.core.common.building.module.CustomCraftingModule;
import steve_gall.minecolonies_tweaks.core.common.command.ModCommands;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigCommon;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;
import steve_gall.minecolonies_tweaks.core.common.crafting.CustomizableRecipeStorageFactory;
import steve_gall.minecolonies_tweaks.core.common.item.CompostDispenseItemBehavior;
import steve_gall.minecolonies_tweaks.core.common.network.NetworkChannel;
import steve_gall.minecolonies_tweaks.core.common.requestsystem.CustomizableDeliverableRequest;
import steve_gall.minecolonies_tweaks.core.common.requestsystem.CustomizableDeliverableRequestFactory;

@Mod(MineColoniesTweaks.MOD_ID)
public class MineColoniesTweaks
{
	public static final String MOD_ID = "minecolonies_tweaks";
	public static final Logger LOGGER = LogManager.getLogger();

	private static NetworkChannel NETWORK;

	public MineColoniesTweaks()
	{
		var modLoadingContext = ModLoadingContext.get();
		modLoadingContext.registerConfig(ModConfig.Type.CLIENT, MineColoniesTweaksConfigClient.SPEC);
		modLoadingContext.registerConfig(ModConfig.Type.COMMON, MineColoniesTweaksConfigCommon.SPEC);
		modLoadingContext.registerConfig(ModConfig.Type.SERVER, MineColoniesTweaksConfigServer.SPEC);

		var fml_bus = FMLJavaModLoadingContext.get().getModEventBus();
		steve_gall.minecolonies_tweaks.core.common.init.ModItems.REGISTER.register(fml_bus);
		steve_gall.minecolonies_tweaks.core.common.init.ModRecipes.SERIALIZERS.register(fml_bus);
		fml_bus.addListener(this::onFMLCommonSetup);
		fml_bus.addListener(this::onFMLLoadComplete);

		var forge_bus = MinecraftForge.EVENT_BUS;
		forge_bus.addListener((RegisterCommandsEvent e) -> ModCommands.register(e.getDispatcher()));

		NETWORK = new NetworkChannel("main");
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MineColoniesTweaksClient::new);
	}

	private void onFMLCommonSetup(FMLCommonSetupEvent e)
	{
		e.enqueueWork(() ->
		{
			StandardFactoryController.getInstance().registerNewFactory(new CustomizableRecipeStorageFactory());
			StandardFactoryController.getInstance().registerNewFactory(new CustomizableDeliverableRequestFactory());

			CustomCraftingModule.loadCustomCraftingModules();

			DispenserBlock.registerBehavior(ModItems.compost, new CompostDispenseItemBehavior());

			this.registerCompostables();
		});
	}

	private void registerCompostables()
	{
		var leave = 0.30F;
		this.registerCompostable(ModItems.mistletoe, leave);
	}

	private void registerCompostable(ItemLike itemLike, float chance)
	{
		ComposterBlock.COMPOSTABLES.put(itemLike.asItem(), chance);
	}

	private void onFMLLoadComplete(FMLLoadCompleteEvent e)
	{
		RequestMappingHandler.registerRequestableTypeMapping(CustomizableDeliverable.class, CustomizableDeliverableRequest.class);
	}

	public static NetworkChannel network()
	{
		return NETWORK;
	}

	public static ResourceLocation rl(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}

}
