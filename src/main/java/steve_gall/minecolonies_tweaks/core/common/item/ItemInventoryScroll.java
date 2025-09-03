package steve_gall.minecolonies_tweaks.core.common.item;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.client.gui.WindowHutAllInventory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemInventoryScroll extends ItemBuildingLinkScroll
{
	public static final Component TOOLTIP = Component.translatable("item.minecolonies_tweaks.inventoryscroll.tooltip");

	public ItemInventoryScroll(Item.Properties properites)
	{
		super(properites);
	}

	@Override
	protected void openWindow(@NotNull ItemStack stack, @Nullable Player player, @Nullable IBuildingView buildingView)
	{
		new WindowHutAllInventory(buildingView, null).open();
	}

	@Override
	public boolean testForLink(@NotNull IBuilding building)
	{
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
	{
		tooltip.add(TOOLTIP);
		super.appendHoverText(stack, context, tooltip, flag);
	}

}
