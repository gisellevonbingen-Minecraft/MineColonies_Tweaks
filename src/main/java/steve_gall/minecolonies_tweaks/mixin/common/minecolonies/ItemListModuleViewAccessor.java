package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.core.colony.buildings.moduleviews.ItemListModuleView;

@Mixin(value = ItemListModuleView.class, remap = false)
public interface ItemListModuleViewAccessor
{
	@Accessor(value = "listsOfItems", remap = false)
	List<ItemStorage> getListsOfItems();
}
