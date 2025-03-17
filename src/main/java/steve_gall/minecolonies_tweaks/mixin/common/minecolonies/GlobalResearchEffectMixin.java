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

	private String command = null;

	@Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void init(CompoundTag nbt, CallbackInfo ci)
	{
		if (nbt.contains(TAG_COMMAND, Tag.TAG_STRING))
		{
			this.command = nbt.getString(TAG_COMMAND);
		}

	}

	@Inject(method = "writeToNBT", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void writeToNBT(CallbackInfoReturnable<CompoundTag> cir)
	{
		var nbt = cir.getReturnValue();

		if (this.command != null)
		{
			nbt.putString(TAG_COMMAND, this.command);
		}

	}

	@Override
	public @Nullable String minecolonies_tweaks$getCommand()
	{
		return this.command;
	}

	@Override
	public void minecolonies_tweaks$setCommand(@Nullable String command)
	{
		this.command = command;
	}

}
