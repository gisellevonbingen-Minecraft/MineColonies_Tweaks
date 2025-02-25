package steve_gall.minecolonies_tweaks.core.common;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@SuppressWarnings({"removal", "deprecation"})
public class CuriosCompat
{
	public static final String MOD_ID = "curios";

	public static void sendInterModComms()
	{
		InterModComms.sendTo(MOD_ID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CURIO.getMessageBuilder().build());
	}

	public static final IItemHandlerModifiable getEquippedCurios(Player player)
	{
		return CuriosApi.getCuriosHelper().getEquippedCurios(player).orElse(null);
	}

	private CuriosCompat()
	{

	}

}
