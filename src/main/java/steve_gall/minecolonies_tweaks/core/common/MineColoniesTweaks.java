package steve_gall.minecolonies_tweaks.core.common;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.manager.RequestMappingHandler;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import steve_gall.minecolonies_tweaks.api.common.SerializationIds;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableDeliverable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.RequestableObjectRegistry;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.resolvers.CustomizableRequestResolverFactory;
import steve_gall.minecolonies_tweaks.core.client.MineColoniesTweaksClient;
import steve_gall.minecolonies_tweaks.core.client.gui.ResourceScrollBookInventoryScreen;
import steve_gall.minecolonies_tweaks.core.common.building.module.CustomCraftingModule;
import steve_gall.minecolonies_tweaks.core.common.command.MCTweaksCommands;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigClient;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigCommon;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;
import steve_gall.minecolonies_tweaks.core.common.crafting.CustomizableRecipeStorageFactory;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksBuildingModules;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksItems;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksMenuTypes;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksRecipes;
import steve_gall.minecolonies_tweaks.core.common.item.CompostDispenseItemBehavior;
import steve_gall.minecolonies_tweaks.core.common.network.NetworkChannel;
import steve_gall.minecolonies_tweaks.core.common.requestsystem.CustomizableDeliverableRequest;
import steve_gall.minecolonies_tweaks.core.common.requestsystem.CustomizableDeliverableRequestFactory;
import steve_gall.minecolonies_tweaks.core.common.requestsystem.CustomizableRequestableRequest;
import steve_gall.minecolonies_tweaks.core.common.requestsystem.CustomizableRequestableRequestFactory;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCost;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCostResolver;

@Mod(MineColoniesTweaks.MOD_ID)
public class MineColoniesTweaks
{
	public static final String MOD_ID = "minecolonies_tweaks";
	public static final Logger LOGGER = LogManager.getLogger();

	private static NetworkChannel NETWORK;

	public MineColoniesTweaks()
	{
		var modLoadingContext = ModLoadingContext.get();
		modLoadingContext.registerConfig(ModConfig.Type.CLIENT, MCTweaksConfigClient.SPEC);
		modLoadingContext.registerConfig(ModConfig.Type.COMMON, MCTweaksConfigCommon.SPEC);
		modLoadingContext.registerConfig(ModConfig.Type.SERVER, MCTweaksConfigServer.SPEC);

		var fml_bus = FMLJavaModLoadingContext.get().getModEventBus();
		MCTweaksItems.REGISTER.register(fml_bus);
		MCTweaksRecipes.SERIALIZERS.register(fml_bus);
		MCTweaksMenuTypes.REGISTER.register(fml_bus);
		fml_bus.addListener(this::onFMLCommonSetup);
		fml_bus.addListener(this::onFMLClientSetup);
		fml_bus.addListener(this::onFMLLoadComplete);
		fml_bus.addListener(this::onInterModEnqueue);

		var forge_bus = MinecraftForge.EVENT_BUS;
		forge_bus.addListener((RegisterCommandsEvent e) -> MCTweaksCommands.register(e.getDispatcher()));
		forge_bus.register(new CommonForgeEventHandler());

		NETWORK = new NetworkChannel("main");
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MineColoniesTweaksClient::new);
	}

	private void onFMLCommonSetup(FMLCommonSetupEvent e)
	{
		e.enqueueWork(() ->
		{
			StandardFactoryController.getInstance().registerNewFactory(new CustomizableRequestableRequestFactory());
			StandardFactoryController.getInstance().registerNewFactory(new CustomizableDeliverableRequestFactory());
			StandardFactoryController.getInstance().registerNewFactory(new CustomizableRecipeStorageFactory());

			StandardFactoryController.getInstance().registerNewFactory(new CustomizableRequestResolverFactory<>(ResearchCostResolver.class, SerializationIds.RESEARCH_COST, ResearchCostResolver::serialize, ResearchCostResolver::deserialize));
			RequestableObjectRegistry.INSTANCE.register(ResearchCost.ID, ResearchCost::serialize, ResearchCost::deserialize);

			CustomCraftingModule.loadCustomCraftingModules();

			for (var buildingEntry : Arrays.asList(ModBuildings.alchemist, ModBuildings.blacksmith, ModBuildings.concreteMixer, ModBuildings.crusher, ModBuildings.dyer, ModBuildings.fletcher, ModBuildings.glassblower, ModBuildings.mechanic, ModBuildings.plantation, ModBuildings.postBox, ModBuildings.sawmill, ModBuildings.stoneMason, ModBuildings.stoneSmelter, ModBuildings.university))
			{
				var moduleProducers = buildingEntry.get().getModuleProducers();

				if (!moduleProducers.contains(BuildingModules.MIN_STOCK))
				{
					moduleProducers.add(BuildingModules.MIN_STOCK);
				}

			}

			ModBuildings.university.get().getModuleProducers().add(MCTweaksBuildingModules.RESEARCH_COST_RESOLVER);

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
		RequestMappingHandler.registerRequestableTypeMapping(CustomizableRequestable.class, CustomizableRequestableRequest.class);
		RequestMappingHandler.registerRequestableTypeMapping(CustomizableDeliverable.class, CustomizableDeliverableRequest.class);
	}

	private void onFMLClientSetup(FMLClientSetupEvent e)
	{
		MenuScreens.register(MCTweaksMenuTypes.RESOURCESCROLL_BOOK_INVENTORY.get(), ResourceScrollBookInventoryScreen::new);
	}

	private void onInterModEnqueue(InterModEnqueueEvent event)
	{
		if (ModList.get().isLoaded(CuriosCompat.MOD_ID))
		{
			CuriosCompat.sendInterModComms();
		}

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
