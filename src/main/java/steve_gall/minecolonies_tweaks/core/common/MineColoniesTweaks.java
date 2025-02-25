package steve_gall.minecolonies_tweaks.core.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.manager.RequestMappingHandler;
import com.minecolonies.api.creativetab.ModCreativeTabs;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.items.ModItems;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import steve_gall.minecolonies_tweaks.api.common.requestsystem.CustomizableDeliverable;
import steve_gall.minecolonies_tweaks.api.common.tool.CustomToolType;
import steve_gall.minecolonies_tweaks.core.client.MineColoniesTweaksClient;
import steve_gall.minecolonies_tweaks.core.client.gui.ResourceScrollBookInventoryScreen;
import steve_gall.minecolonies_tweaks.core.common.block.MinecoloniesCropBlockExtension;
import steve_gall.minecolonies_tweaks.core.common.building.module.CustomCraftingModule;
import steve_gall.minecolonies_tweaks.core.common.command.ModCommands;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigClient;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigCommon;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;
import steve_gall.minecolonies_tweaks.core.common.crafting.CustomizableRecipeStorageFactory;
import steve_gall.minecolonies_tweaks.core.common.init.ModEquipmentTypes;
import steve_gall.minecolonies_tweaks.core.common.init.ModMenuTypes;
import steve_gall.minecolonies_tweaks.core.common.item.CompostDispenseItemBehavior;
import steve_gall.minecolonies_tweaks.core.common.item.ItemCropExtension;
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
		ModMenuTypes.REGISTER.register(fml_bus);
		ModEquipmentTypes.REGISTER.register(fml_bus);
		fml_bus.addListener(this::onFMLCommonSetup);
		fml_bus.addListener(this::onFMLLoadComplete);
		fml_bus.addListener(this::onFMLClientSetup);
		fml_bus.addListener(this::onRegister);
		fml_bus.addListener((ModConfigEvent.Loading e) -> this.onConfigReload(e));
		fml_bus.addListener((ModConfigEvent.Reloading e) -> this.onConfigReload(e));
		fml_bus.addListener(this::onBuildCreativeModeTabContents);
		fml_bus.addListener(this::onInterModEnqueue);

		var forge_bus = MinecraftForge.EVENT_BUS;
		forge_bus.addListener((RegisterCommandsEvent e) -> ModCommands.register(e.getDispatcher()));
		forge_bus.register(new CommonForgeEventHandler());

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

		var crop = 0.65F;
		this.registerCompostable(ModBlocks.blockBellPepper, crop);
		this.registerCompostable(ModBlocks.blockCabbage, crop);
		this.registerCompostable(ModBlocks.blockChickpea, crop);
		this.registerCompostable(ModBlocks.blockDurum, crop);
		this.registerCompostable(ModBlocks.blockEggplant, crop);
		this.registerCompostable(ModBlocks.blockGarlic, crop);
		this.registerCompostable(ModBlocks.blockSoyBean, crop);
		this.registerCompostable(ModBlocks.blockTomato, crop);
		this.registerCompostable(ModBlocks.blockRice, crop);
		this.registerCompostable(ModBlocks.blockButternutSquash, crop);
		this.registerCompostable(ModBlocks.blockCorn, crop);
		this.registerCompostable(ModBlocks.blockMint, crop);
		this.registerCompostable(ModBlocks.blockNetherPepper, crop);
		this.registerCompostable(ModBlocks.blockPeas, crop);

		var food = 0.85F;
		this.registerCompostable(ModItems.manchet_bread, food);
		this.registerCompostable(ModItems.muffin, food);
		this.registerCompostable(ModItems.lembas_scone, food);
	}

	private void registerCompostable(ItemLike itemLike, float chance)
	{
		ComposterBlock.COMPOSTABLES.put(itemLike.asItem(), chance);
	}

	private void onFMLLoadComplete(FMLLoadCompleteEvent e)
	{
		RequestMappingHandler.registerRequestableTypeMapping(CustomizableDeliverable.class, CustomizableDeliverableRequest.class);
	}

	private void onRegister(RegisterEvent e)
	{
		if (e.getRegistryKey() == ModEquipmentTypes.REGISTER.getRegistryKey())
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
		if (e.getConfig().getSpec() == MineColoniesTweaksConfigServer.SPEC)
		{
			for (var block : ForgeRegistries.BLOCKS.getValues())
			{
				if (block instanceof MinecoloniesCropBlockExtension extension)
				{
					extension.minecolonies_tweaks$onServerConfigReloaded();
				}

			}

			for (var item : ForgeRegistries.ITEMS.getValues())
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
			for (var object : steve_gall.minecolonies_tweaks.core.common.init.ModItems.COLOR_RESOURCE_SCROLLS.values())
			{
				e.accept(object.get());
			}

			e.accept(steve_gall.minecolonies_tweaks.core.common.init.ModItems.RESOURCESCROLL_BOOK.get());
		}

	}

	private void onFMLClientSetup(FMLClientSetupEvent e)
	{
		MenuScreens.register(ModMenuTypes.RESOURCESCROLL_BOOK_INVENTORY.get(), ResourceScrollBookInventoryScreen::new);
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
