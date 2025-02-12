package steve_gall.minecolonies_tweaks.core.common.colony;

import com.minecolonies.api.colony.fields.IField;

import net.minecraft.core.BlockPos;

public interface IColonyViewExtension
{
	IField minecolonies_tweaks$getField(BlockPos pos);
}
