package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.core.colony.Colony;

import net.minecraft.nbt.CompoundTag;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.colony.BatchRepairData;
import steve_gall.minecolonies_tweaks.core.common.colony.ColonyExtension;

@Mixin(value = Colony.class, remap = false)
public abstract class ColonyMixin implements ColonyExtension
{
	@Unique
	private final BatchRepairData minecolonies_tweaks$batchRepair = new BatchRepairData();

	@Inject(method = "read", remap = false, at = @At(value = "TAIL"), cancellable = true)
	public void read(CompoundTag compound, CallbackInfo ci)
	{
		this.minecolonies_tweaks$batchRepair.deserializeNBT(compound.getCompound(MineColoniesTweaks.rl("batch_repair").toString()));
	}

	@Inject(method = "write", remap = false, at = @At(value = "TAIL"), cancellable = true)
	public void write(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir)
	{
		compound.put(MineColoniesTweaks.rl("batch_repair").toString(), this.minecolonies_tweaks$batchRepair.serializeNBT());
	}

	@Override
	public BatchRepairData minecolonies_tweaks$getBatchRepair()
	{
		return this.minecolonies_tweaks$batchRepair;
	}

}
