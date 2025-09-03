package steve_gall.minecolonies_tweaks.core.common;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

@SuppressWarnings({"removal", "deprecation"})
public class CuriosCompat
{
	public static final String MOD_ID = "curios";

	public static final IItemHandlerModifiable getEquippedCurios(Player player)
	{
		return CuriosApi.getCuriosHelper().getEquippedCurios(player).orElse(null);
	}

	private CuriosCompat()
	{

	}

}
