package steve_gall.minecolonies_tweaks.core.common.research;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.ldtteam.blockui.views.BOWindow;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.research.costs.IResearchCost;
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
					var storage = new ItemStorage(stack);
					storage.setAmount(cost.getCount());
					this.selectedCosts.add(storage);
					this.cycle();
				}, false).open();
				break;
			}
			else
			{
				var storage = new ItemStorage(items.get(0).getDefaultInstance());
				storage.setAmount(cost.getCount());
				this.selectedCosts.add(storage);
			}

		}

		if (this.selectedCosts.size() == costsCount)
		{
			this.consumer.accept(new ArrayList<>(this.selectedCosts));
		}

	}

}
