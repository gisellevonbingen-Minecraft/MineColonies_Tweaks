package steve_gall.minecolonies_tweaks.core.common.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.structurize.client.gui.WindowSelectRes;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.util.ItemStackUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public class ResearchCostSelector
{
	public static void open(BOWindow origin, IGlobalResearch research, Consumer<List<ItemStorage>> consumer)
	{
		new ResearchCostSelector(origin, research, consumer).open();
	}

	private final BOWindow origin;
	private final IGlobalResearch research;
	private final List<SizedIngredient> costs;
	private final Consumer<List<ItemStorage>> consumer;

	private List<ItemStorage> selectedCosts;

	private ResearchCostSelector(BOWindow origin, IGlobalResearch research, Consumer<List<ItemStorage>> consumer)
	{
		this.origin = origin;
		this.research = research;
		this.costs = this.research.getCostList();
		this.consumer = consumer;
	}

	public void open()
	{
		this.selectedCosts = new ArrayList<>();
		this.cycle();
	}

	private void cycle()
	{
		var costsCount = this.costs.size();

		for (; this.selectedCosts.size() < costsCount;)
		{
			var cost = this.costs.get(this.selectedCosts.size());
			var items = Arrays.stream(cost.getItems()).map(ItemStack::getItem).toList();

			if (items.size() > 1)
			{
				new WindowSelectRes(this.origin, Component.translatable("minecolonies_tweaks.gui.select_research_cost", ItemStackUtils.getTranslatedName(cost)), null, items.stream().map(ItemStack::new).toList(), (stack, count) ->
				{
					this.selectedCosts.add(new ItemStorage(stack, cost.count()));
					this.cycle();
				}).open();
				break;
			}
			else
			{
				this.selectedCosts.add(new ItemStorage(items.get(0), cost.count()));
			}

		}

		if (this.selectedCosts.size() == costsCount)
		{
			this.consumer.accept(new ArrayList<>(this.selectedCosts));
		}

	}

}
