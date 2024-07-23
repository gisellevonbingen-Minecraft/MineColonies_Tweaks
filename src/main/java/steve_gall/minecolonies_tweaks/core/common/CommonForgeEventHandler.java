package steve_gall.minecolonies_tweaks.core.common;

import com.minecolonies.api.util.constant.TranslationConstants;

import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import steve_gall.minecolonies_tweaks.core.common.init.ModTags;

public class CommonForgeEventHandler
{
	public static final Component GREAT_FOOD_TOOLTIP = Component.translatable(TranslationConstants.TIER_TOOLTIP + 3);
	public static final Component DECENT_FOOD_TOOLTIP = Component.translatable(TranslationConstants.TIER_TOOLTIP + 1);

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent e)
	{
		if (e.getItemStack().is(ModTags.Items.GREAT_FOOD))
		{
			e.getToolTip().add(1, GREAT_FOOD_TOOLTIP);
		}
		else if (e.getItemStack().is(ModTags.Items.DECENT_FOOD))
		{
			e.getToolTip().add(1, DECENT_FOOD_TOOLTIP);
		}

	}

	public CommonForgeEventHandler()
	{

	}

}
