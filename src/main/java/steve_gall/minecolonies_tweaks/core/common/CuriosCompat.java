package steve_gall.minecolonies_tweaks.core.common;

import java.util.function.Predicate;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
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

	public static ItemStack findFirstCurio(Player player, Predicate<ItemStack> predicate)
	{
		var handler = CuriosApi.getCuriosHelper().getEquippedCurios(player).orElse(null);

		if (handler != null)
		{
			for (var i = 0; i < handler.getSlots(); i++)
			{
				var stack = handler.getStackInSlot(i);

				if (predicate.test(stack))
				{
					return stack;
				}

			}

		}

		return ItemStack.EMPTY;
	}

	private CuriosCompat()
	{

	}

}
