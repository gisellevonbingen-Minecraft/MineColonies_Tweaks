package steve_gall.minecolonies_tweaks.core.common.inventory;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

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
		return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
	}

	private InventoryUtils2()
	{

	}

}
