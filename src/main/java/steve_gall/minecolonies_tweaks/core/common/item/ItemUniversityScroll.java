package steve_gall.minecolonies_tweaks.core.common.item;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingUniversity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import steve_gall.minecolonies_tweaks.core.client.gui.UniversityDashboardWindow;

public class ItemUniversityScroll extends ItemBuildingLinkScroll
{
	public static final Component TOOLTIP = Component.translatable("item.minecolonies_tweaks.universityscroll.tooltip");

	public ItemUniversityScroll(Item.Properties properites)
	{
		super(properites);
	}

	@Override
	protected void openWindow(@NotNull ItemStack stack, @Nullable Player player, @Nullable IBuildingView buildingView)
	{
		new UniversityDashboardWindow(buildingView).open();
	}

	@Override
	public boolean testForLink(@NotNull IBuilding building)
	{
		return building instanceof BuildingUniversity;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
	{
		tooltip.add(TOOLTIP);
		super.appendHoverText(stack, context, tooltip, flag);

		var buildingPos = getPos(stack);
		var buildingView = buildingPos != null ? buildingPos.getBuildingView() : null;

		if (buildingView != null)
		{
			var researchInProgress = buildingView.getColony().getResearchManager().getResearchTree().getResearchInProgress().size();
			var limit = buildingView.getBuildingLevel();
			tooltip.add(Component.translatable("com.minecolonies.coremod.gui.research.countinprogress", researchInProgress, limit).withStyle(limit <= researchInProgress ? ChatFormatting.RED : ChatFormatting.WHITE));
		}

	}

}
