package steve_gall.minecolonies_tweaks.core.common.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import steve_gall.minecolonies_tweaks.api.common.building.BuildingPos;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class MCTweaksDataComponents
{
	public static final DeferredRegister<DataComponentType<?>> REGISTER = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MineColoniesTweaks.MOD_ID);

	public static DeferredHolder<DataComponentType<?>, DataComponentType<BuildingPos>> BUILDING_POS = REGISTER.register("building_pos", () -> DataComponentType.<BuildingPos> builder().persistent(BuildingPos.CODEC).networkSynchronized(BuildingPos.STREAM_CODEC).build());
	public static DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> RESOURCESCROLL_BOOK_ITEMS = REGISTER.register("resourcescroll_book_items", () -> DataComponentType.<CompoundTag> builder().persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG).build());
}
