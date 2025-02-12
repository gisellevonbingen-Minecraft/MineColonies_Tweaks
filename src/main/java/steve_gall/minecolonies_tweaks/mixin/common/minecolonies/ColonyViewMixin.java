package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.colony.fields.IField;
import com.minecolonies.core.colony.ColonyView;

import net.minecraft.core.BlockPos;
import steve_gall.minecolonies_tweaks.core.common.colony.IColonyViewExtension;

@Mixin(value = ColonyView.class, remap = false)
public abstract class ColonyViewMixin implements IColonyViewExtension
{
	@Unique
	private final Map<BlockPos, IField> minecolonies_tweaks$pos2fields = new HashMap<>();

	@Inject(method = "handleColonyFieldViewUpdateMessage", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void handleColonyFieldViewUpdateMessage(Set<IField> fields, CallbackInfo ci)
	{
		this.minecolonies_tweaks$pos2fields.clear();

		for (var field : fields)
		{
			this.minecolonies_tweaks$pos2fields.put(field.getPosition(), field);
		}

	}

	@Override
	public IField minecolonies_tweaks$getField(BlockPos pos)
	{
		return this.minecolonies_tweaks$pos2fields.get(pos);
	}

}
