package steve_gall.minecolonies_tweaks.core.common.item;

import com.minecolonies.api.items.IMinecoloniesFoodItem;
import com.minecolonies.api.items.ModItems;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksTags;

public class FoodUtils2
{
	public static Item getTierRepresentedFood(ItemStack foodStack)
	{
		var item = foodStack.getItem();

		if (item instanceof IMinecoloniesFoodItem)
		{
			return item;
		}

		var tier = MCTweaksTags.Items.getFoodTier(foodStack::is);
		return getTierRepresentedFood(tier, item);
	}

	public static Item getTierRepresentedFood(int tier, Item fallback)
	{
		if (tier == 3)
		{
			return ModItems.hand_pie;
		}
		else if (tier == 2)
		{
			return ModItems.manchet_bread;
		}
		else if (tier == 1)
		{
			return ModItems.cheddar_cheese;
		}
		else
		{
			return fallback;
		}

	}

	private FoodUtils2()
	{

	}

}
