package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.core.research.GlobalResearchEffect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import steve_gall.minecolonies_tweaks.core.common.research.GlobalResearchEffectExtension;

@Mixin(value = GlobalResearchEffect.class, remap = false)
public abstract class GlobalResearchEffectMixin implements GlobalResearchEffectExtension
{
	private static final String TAG_COMMAND = "command";
	private static final String TAG_IS_OFFLINE_RUNNABLE = "isOfflineRunnable";

	private String minecolonies_tweaks$command = null;
	private boolean minecolonies_tweaks$isOfflineRunnable = false;

	@Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void init(CompoundTag nbt, CallbackInfo ci)
	{
		if (nbt.contains(TAG_COMMAND, Tag.TAG_STRING))
		{
			this.minecolonies_tweaks$command = nbt.getString(TAG_COMMAND);
		}

		this.minecolonies_tweaks$isOfflineRunnable = nbt.getBoolean(TAG_IS_OFFLINE_RUNNABLE);
	}

	@Inject(method = "writeToNBT", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void writeToNBT(CallbackInfoReturnable<CompoundTag> cir)
	{
		var nbt = cir.getReturnValue();

		if (this.minecolonies_tweaks$command != null)
		{
			nbt.putString(TAG_COMMAND, this.minecolonies_tweaks$command);
		}

		nbt.putBoolean(TAG_IS_OFFLINE_RUNNABLE, this.minecolonies_tweaks$isOfflineRunnable);
	}

	@Override
	public @Nullable String minecolonies_tweaks$getCommand()
	{
		return this.minecolonies_tweaks$command;
	}

	@Override
	public void minecolonies_tweaks$setCommand(@Nullable String command)
	{
		this.minecolonies_tweaks$command = command;
	}

	@Override
	public boolean minecolonies_tweaks$isOfflineRunnable()
	{
		return this.minecolonies_tweaks$isOfflineRunnable;
	}

	@Override
	public void minecolonies_tweaks$setOfflineRunnable(boolean offlineRunnable)
	{
		this.minecolonies_tweaks$isOfflineRunnable = offlineRunnable;
	}

}
