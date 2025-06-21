package steve_gall.minecolonies_tweaks.core.common.inventory;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class InventoryUtils2
{
	public static InvWrapper wrapWithBuilding(InvWrapper inv, Level level, BlockPos pos)
	{
		var itemHandlers = new ArrayList<IItemHandlerModifiable>();

		if (getBlockEntityItemHandler(level, pos) instanceof IItemHandlerModifiable itemHandlerModifiable)
		{
			itemHandlers.add(itemHandlerModifiable);
		}

		itemHandlers.add(inv);
		return new CombinedInvWrapper(inv, itemHandlers.toArray(IItemHandlerModifiable[]::new));
	}

	private static IItemHandler getBlockEntityItemHandler(Level level, BlockPos pos)
	{
		var be = level.getBlockEntity(pos);

		if (be != null)
		{
			var cap = be.getCapability(ForgeCapabilities.ITEM_HANDLER);
			return cap == null ? null : cap.orElse(null);
		}

		return null;
	}

	private InventoryUtils2()
	{

	}

}
