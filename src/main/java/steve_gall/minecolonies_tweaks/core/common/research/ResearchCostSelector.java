package steve_gall.minecolonies_tweaks.core.common.research;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.ldtteam.blockui.views.BOWindow;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.research.IResearchCost;
import com.minecolonies.core.client.gui.WindowSelectRes;

public class ResearchCostSelector
{
	public static void open(BOWindow origin, IGlobalResearch research, Consumer<List<ItemStorage>> consumer)
	{
		new ResearchCostSelector(origin, research, consumer).open();
	}

	private final BOWindow origin;
	private final IGlobalResearch research;
	private final List<IResearchCost> costs;
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
			var items = cost.getItems();

			if (items.size() > 1)
			{
				new WindowSelectRes(this.origin, stack -> items.contains(stack.getItem()), (stack, count) ->
				{
					this.selectedCosts.add(new ItemStorage(stack, cost.getCount()));
					this.cycle();
				}, false).open();
				break;
			}
			else
			{
				this.selectedCosts.add(new ItemStorage(items.get(0), cost.getCount()));
			}

		}

		if (this.selectedCosts.size() == costsCount)
		{
			this.consumer.accept(new ArrayList<>(this.selectedCosts));
		}

	}

}
