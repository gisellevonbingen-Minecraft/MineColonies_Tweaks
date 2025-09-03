package steve_gall.minecolonies_tweaks.core.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.inventory.ResourceScrollBookInventoryMenu;

public class MCTweaksMenuTypes
{
	public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, MineColoniesTweaks.MOD_ID);
	public static final DeferredHolder<MenuType<?>, MenuType<ResourceScrollBookInventoryMenu>> RESOURCESCROLL_BOOK_INVENTORY = REGISTER.register("resourcescroll_book_inventory", () -> IMenuTypeExtension.create(ResourceScrollBookInventoryMenu::create));

	private MCTweaksMenuTypes()
	{

	}

}
