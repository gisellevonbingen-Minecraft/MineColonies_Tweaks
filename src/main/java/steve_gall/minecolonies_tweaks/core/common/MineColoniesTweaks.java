package steve_gall.minecolonies_tweaks.core.common;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.manager.RequestMappingHandler;
import com.minecolonies.api.creativetab.ModCreativeTabs;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import steve_gall.minecolonies_tweaks.api.common.SerializationIds;
import steve_gall.minecolonies_tweaks.api.common.network.MessageRegistrar;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableDeliverable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableRequestable;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.RequestableObjectRegistry;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.resolvers.CustomizableRequestResolverFactory;
import steve_gall.minecolonies_tweaks.api.common.tool.CustomToolType;
import steve_gall.minecolonies_tweaks.core.client.MineColoniesTweaksClient;
import steve_gall.minecolonies_tweaks.core.common.block.MinecoloniesCropBlockExtension;
import steve_gall.minecolonies_tweaks.core.common.building.module.CustomCraftingModule;
import steve_gall.minecolonies_tweaks.core.common.command.MCTweaksCommands;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigClient;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigCommon;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;
import steve_gall.minecolonies_tweaks.core.common.crafting.CustomizableRecipeStorageFactory;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksBuildingModules;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksDataComponents;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksEquipmentTypes;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksItems;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksMenuTypes;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksRecipes;
import steve_gall.minecolonies_tweaks.core.common.item.CompostDispenseItemBehavior;
import steve_gall.minecolonies_tweaks.core.common.item.ItemCropExtension;
import steve_gall.minecolonies_tweaks.core.common.network.MCTweaksMessagesRegistrar;
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

	public MineColoniesTweaks(FMLModContainer modContainer, Dist dist)
	{
		modContainer.registerConfig(ModConfig.Type.CLIENT, MCTweaksConfigClient.SPEC);
		modContainer.registerConfig(ModConfig.Type.COMMON, MCTweaksConfigCommon.SPEC);
		modContainer.registerConfig(ModConfig.Type.SERVER, MCTweaksConfigServer.SPEC);

		var fml_bus = modContainer.getEventBus();
		MCTweaksDataComponents.REGISTER.register(fml_bus);
		MCTweaksItems.REGISTER.register(fml_bus);
		MCTweaksRecipes.SERIALIZERS.register(fml_bus);
		MCTweaksMenuTypes.REGISTER.register(fml_bus);
		MCTweaksEquipmentTypes.REGISTER.register(fml_bus);
		fml_bus.addListener(this::onFMLCommonSetup);
		fml_bus.addListener(this::onFMLLoadComplete);
		fml_bus.addListener(this::onRegister);
		fml_bus.addListener((ModConfigEvent.Loading e) -> this.onConfigReload(e));
		fml_bus.addListener((ModConfigEvent.Reloading e) -> this.onConfigReload(e));
		fml_bus.addListener(this::onBuildCreativeModeTabContents);
		fml_bus.addListener(this::onRegisterPayloadHandlers);

		var forge_bus = NeoForge.EVENT_BUS;
		forge_bus.addListener((RegisterCommandsEvent e) -> MCTweaksCommands.register(e.getDispatcher()));
		forge_bus.register(new CommonForgeEventHandler());

		if (dist.isClient())
		{
			new MineColoniesTweaksClient(modContainer);
		}

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

			for (var buildingEntry : Arrays.asList(ModBuildings.alchemist, ModBuildings.blacksmith, ModBuildings.concreteMixer, ModBuildings.crusher, ModBuildings.dyer, ModBuildings.fletcher, ModBuildings.glassblower, ModBuildings.mechanic, ModBuildings.plantation, ModBuildings.sawmill, ModBuildings.stoneMason, ModBuildings.stoneSmelter, ModBuildings.university))
			{
				var moduleProducers = buildingEntry.get().getModuleProducers();

				if (!moduleProducers.contains(BuildingModules.MIN_STOCK))
				{
					moduleProducers.add(BuildingModules.MIN_STOCK);
				}

			}

			ModBuildings.university.get().getModuleProducers().add(MCTweaksBuildingModules.RESEARCH_COST_RESOLVER);
			ModBuildings.wareHouse.get().getModuleProducers().add(MCTweaksBuildingModules.MAXIMUM_STOCK);
			ModBuildings.library.get().getModuleProducers().add(MCTweaksBuildingModules.STUDY_ITEM_BLACKLIST);

			DispenserBlock.registerBehavior(ModItems.compost, new CompostDispenseItemBehavior());
		});
	}

	private void onFMLLoadComplete(FMLLoadCompleteEvent e)
	{
		RequestMappingHandler.registerRequestableTypeMapping(CustomizableRequestable.class, CustomizableRequestableRequest.class);
		RequestMappingHandler.registerRequestableTypeMapping(CustomizableDeliverable.class, CustomizableDeliverableRequest.class);
	}

	private void onRegister(RegisterEvent e)
	{
		if (e.getRegistryKey() == IMinecoloniesAPI.getInstance().getBuildingRegistry().key())
		{
			MCTweaksBuildingModules.init();
		}
		else if (e.getRegistryKey() == MCTweaksEquipmentTypes.REGISTER.getRegistryKey())
		{
			CustomToolType.init();
			@SuppressWarnings("unchecked")
			var registryKey = (ResourceKey<? extends Registry<EquipmentTypeEntry>>) e.getRegistryKey();

			for (var type : CustomToolType.list())
			{
				e.register(registryKey, type.getName(), () ->
				{
					var builder = new EquipmentTypeEntry.Builder();
					builder.setRegistryName(type.getName());
					builder.setDisplayName(type.getDisplayName());
					builder.setIsEquipment((stack, b) -> false);
					builder.setEquipmentLevel((stack, b) -> -1);

					var build = builder.build();
					type.pair(build);
					return build;
				});
			}

		}

	}

	private void onConfigReload(ModConfigEvent e)
	{
		if (e.getConfig().getSpec() == MCTweaksConfigServer.SPEC)
		{
			for (var block : BuiltInRegistries.BLOCK)
			{
				if (block instanceof MinecoloniesCropBlockExtension extension)
				{
					extension.minecolonies_tweaks$onServerConfigReloaded();
				}

			}

			for (var item : BuiltInRegistries.ITEM)
			{
				if (item instanceof ItemCropExtension extension)
				{
					extension.minecolonies_tweaks$onServerConfigReloaded();
				}

			}

		}

	}

	private void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent e)
	{
		if (e.getTab() == ModCreativeTabs.GENERAL.get())
		{
			for (var object : MCTweaksItems.COLOR_RESOURCE_SCROLLS.values())
			{
				e.accept(object.get());
			}

			e.accept(MCTweaksItems.INVENTORYSCROLL.get());

			for (var object : MCTweaksItems.COLOR_INVENTORY_SCROLLS.values())
			{
				e.accept(object.get());
			}

			e.accept(MCTweaksItems.UNIVERSITYSCROLL.get());
			e.accept(MCTweaksItems.RESOURCESCROLL_BOOK.get());
			e.accept(MCTweaksItems.COPYSCROLL.get());
		}

	}

	private void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event)
	{
		var modVersion = ModList.get().getModContainerById(MOD_ID).get().getModInfo().getVersion().toString();
		var registry = new MessageRegistrar(event.registrar(MOD_ID).versioned(modVersion));
		MCTweaksMessagesRegistrar.register(registry);
	}

	public static ResourceLocation rl(String path)
	{
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

}
