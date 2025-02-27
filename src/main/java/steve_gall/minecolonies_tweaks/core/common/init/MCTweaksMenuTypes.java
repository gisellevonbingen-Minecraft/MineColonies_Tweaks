package steve_gall.minecolonies_tweaks.core.common.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.inventory.ResourceScrollBookInventoryMenu;

public class MCTweaksMenuTypes
{
	public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MineColoniesTweaks.MOD_ID);
	public static final RegistryObject<MenuType<ResourceScrollBookInventoryMenu>> RESOURCESCROLL_BOOK_INVENTORY = REGISTER.register("resourcescroll_book_inventory", () -> IForgeMenuType.create(ResourceScrollBookInventoryMenu::create));

	private MCTweaksMenuTypes()
	{

	}

}
