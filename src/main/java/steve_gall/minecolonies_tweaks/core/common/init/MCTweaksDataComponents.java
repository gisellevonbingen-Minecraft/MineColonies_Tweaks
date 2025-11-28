package steve_gall.minecolonies_tweaks.core.common.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import steve_gall.minecolonies_tweaks.api.common.building.BuildingPos;
import steve_gall.minecolonies_tweaks.api.common.building.module.ModulePos;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.item.ItemCopyScroll;

public class MCTweaksDataComponents
{
	public static final DeferredRegister<DataComponentType<?>> REGISTER = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MineColoniesTweaks.MOD_ID);

	public static DeferredHolder<DataComponentType<?>, DataComponentType<BuildingPos>> BUILDING_POS = REGISTER.register("building_pos", () -> DataComponentType.<BuildingPos> builder().persistent(BuildingPos.CODEC).networkSynchronized(BuildingPos.STREAM_CODEC).build());
	public static DeferredHolder<DataComponentType<?>, DataComponentType<ModulePos>> MODULE_POS = REGISTER.register("module_pos", () -> DataComponentType.<ModulePos> builder().persistent(ModulePos.CODEC).networkSynchronized(ModulePos.STREAM_CODEC).build());
	public static DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> RESOURCESCROLL_BOOK_ITEMS = REGISTER.register("resourcescroll_book_items", () -> DataComponentType.<CompoundTag> builder().persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG).build());
	public static DeferredHolder<DataComponentType<?>, DataComponentType<ItemCopyScroll.CopyData>> COPYSCROLL_DATA = REGISTER.register("copyscroll_data", () -> DataComponentType.<ItemCopyScroll.CopyData> builder().persistent(ItemCopyScroll.CopyData.CODEC).networkSynchronized(ItemCopyScroll.CopyData.STREAM_CODEC).build());
}
