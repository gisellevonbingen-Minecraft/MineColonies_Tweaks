package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.core.colony.Colony;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.colony.BatchRepairData;
import steve_gall.minecolonies_tweaks.core.common.colony.BatchUpgradeData;
import steve_gall.minecolonies_tweaks.core.common.colony.ColonyExtension;

@Mixin(value = Colony.class, remap = false)
public abstract class ColonyMixin implements ColonyExtension
{
	@Unique
	private final BatchRepairData minecolonies_tweaks$batchRepair = new BatchRepairData();
	@Unique
	private final BatchUpgradeData minecolonies_tweaks$batchUpgrade = new BatchUpgradeData();
	@Unique
	private final ArrayList<String> minecolonies_tweaks$commandQueue = new ArrayList<>();

	@Inject(method = "read", remap = false, at = @At(value = "TAIL"), cancellable = true)
	public void read(CompoundTag compound, CallbackInfo ci)
	{
		this.minecolonies_tweaks$batchRepair.deserializeNBT(compound.getCompound(MineColoniesTweaks.rl("batch_repair").toString()));
		this.minecolonies_tweaks$batchUpgrade.deserializeNBT(compound.getCompound(MineColoniesTweaks.rl("batch_upgrade").toString()));

		var commandQueue = compound.getList(MineColoniesTweaks.rl("command_queue").toString(), Tag.TAG_STRING);
		this.minecolonies_tweaks$commandQueue.clear();

		for (var i = 0; i < commandQueue.size(); i++)
		{
			this.minecolonies_tweaks$commandQueue.add(commandQueue.getString(i));
		}

	}

	@Inject(method = "write", remap = false, at = @At(value = "TAIL"), cancellable = true)
	public void write(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir)
	{
		compound.put(MineColoniesTweaks.rl("batch_repair").toString(), this.minecolonies_tweaks$batchRepair.serializeNBT());
		compound.put(MineColoniesTweaks.rl("batch_upgrade").toString(), this.minecolonies_tweaks$batchUpgrade.serializeNBT());

		var commandQueue = new ListTag();

		for (var command : this.minecolonies_tweaks$commandQueue)
		{
			commandQueue.add(StringTag.valueOf(command));
		}

		compound.put(MineColoniesTweaks.rl("command_queue").toString(), commandQueue);
	}

	@Override
	public BatchRepairData minecolonies_tweaks$getBatchRepair()
	{
		return this.minecolonies_tweaks$batchRepair;
	}

	@Override
	public BatchUpgradeData minecolonies_tweaks$getBatchUpgrade()
	{
		return this.minecolonies_tweaks$batchUpgrade;
	}

	@Override
	public ArrayList<String> minecolonies_tweaks$getCommandQueue()
	{
		return this.minecolonies_tweaks$commandQueue;
	}

}
