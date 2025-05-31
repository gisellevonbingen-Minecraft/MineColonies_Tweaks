package steve_gall.minecolonies_tweaks.core.common.building.module;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.ldtteam.blockui.views.BOWindow;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import com.minecolonies.api.colony.buildings.modules.ITickingModule;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.research.util.ResearchConstants;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.ItemStackUtils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.core.client.gui.MaximumStockModuleWindow;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;
import steve_gall.minecolonies_tweaks.core.common.inventory.BlackHoleItemHandler;
import steve_gall.minecolonies_tweaks.core.common.network.message.MaximumStockUpdateMessage;

public class MaximumStockModule extends AbstractBuildingModule implements IPersistentModule, ITickingModule
{
	protected final Object2IntMap<ItemStorage> maximumStock = new Object2IntOpenHashMap<>();

	public MaximumStockModule()
	{

	}

	public boolean hasReachedLimit()
	{
		return this.maximumStock.size() >= this.getKindsLimit();
	}

	public int getKindsLimit()
	{
		var building = this.building;
		var increase = 1.0D + building.getColony().getResearchManager().getResearchEffects().getEffectStrength(ResearchConstants.MINIMUM_STOCK);
		return (int) (building.getBuildingLevel() * increase * MCTweaksConfigServer.INSTANCE.jobs.maximumStockKindsPerLevel.get());
	}

	public List<Entry> getList()
	{
		var list = new ArrayList<Entry>();

		for (var entry : this.maximumStock.object2IntEntrySet())
		{
			list.add(new Entry(entry.getKey().getItemStack().copy(), entry.getIntValue()));
		}

		return list;
	}

	public boolean contains(ItemStack stack)
	{
		return this.maximumStock.containsKey(new ItemStorage(stack));
	}

	public void add(ItemStack stack, int quantity)
	{
		if (stack.isEmpty())
		{
			return;
		}

		this.maximumStock.put(new ItemStorage(stack.copy()), quantity);
		this.markDirty();
	}

	public void remove(ItemStack stack)
	{
		this.maximumStock.removeInt(new ItemStorage(stack));
		this.markDirty();
	}

	@Override
	public void onColonyTick(@NotNull IColony colony)
	{
		for (var entry : this.maximumStock.object2IntEntrySet())
		{
			var stack = entry.getKey().getItemStack();

			if (stack.isEmpty())
			{
				continue;
			}

			var maxStackSize = stack.getMaxStackSize();
			var target = entry.getIntValue() * maxStackSize;
			Predicate<ItemStack> predicate = s -> this.matchesItem(s, stack);
			var count = InventoryUtils.hasBuildingEnoughElseCount(this.building, predicate, target);
			var over = count - target;

			if (over > 0)
			{
				InventoryUtils.transferXOfFirstSlotInProviderWithIntoNextFreeSlotInItemHandler(this.building, predicate, over, BlackHoleItemHandler.INSTANCE);
			}

		}

	}

	public boolean matchesItem(ItemStack stack1, ItemStack stack2)
	{
		return ItemStackUtils.compareItemStacksIgnoreStackSize(stack1, stack2, false, false);
	}

	@Override
	public void deserializeNBT(CompoundTag compound)
	{
		this.maximumStock.clear();

		var maximumStackTag = compound.getList("maximumStock", Tag.TAG_COMPOUND);

		for (var i = 0; i < maximumStackTag.size(); i++)
		{
			var entryTag = maximumStackTag.getCompound(i);
			var stack = ItemStack.of(entryTag.getCompound("key"));

			if (stack.isEmpty())
			{
				continue;
			}

			var key = new ItemStorage(stack);
			var value = entryTag.getInt("value");
			this.maximumStock.put(key, value);
		}

	}

	@Override
	public void serializeNBT(CompoundTag compound)
	{
		var maximumStackTag = new ListTag();

		for (var entry : this.maximumStock.object2IntEntrySet())
		{
			var entryTag = new CompoundTag();
			entryTag.put("key", entry.getKey().getItemStack().serializeNBT());
			entryTag.putInt("value", entry.getIntValue());
			maximumStackTag.add(entryTag);
		}

		compound.put("maximumStock", maximumStackTag);
	}

	@Override
	public void serializeToView(FriendlyByteBuf buf)
	{
		super.serializeToView(buf);

		buf.writeInt(this.maximumStock.size());

		for (var entry : this.maximumStock.object2IntEntrySet())
		{
			buf.writeItem(entry.getKey().getItemStack());
			buf.writeInt(entry.getIntValue());
		}

	}

	public static class View extends AbstractBuildingModuleView
	{
		protected final Object2IntMap<ItemStorage> maximumStock = new Object2IntOpenHashMap<>();

		@Override
		public void deserialize(@NotNull FriendlyByteBuf buf)
		{
			this.maximumStock.clear();

			var size = buf.readInt();

			for (var i = 0; i < size; i++)
			{
				var key = new ItemStorage(buf.readItem());
				var value = buf.readInt();
				this.maximumStock.put(key, value);
			}

		}

		public List<Entry> getList()
		{
			var list = new ArrayList<Entry>();

			for (var entry : this.maximumStock.object2IntEntrySet())
			{
				list.add(new Entry(entry.getKey().getItemStack().copy(), entry.getIntValue()));
			}

			return list;
		}

		public boolean hasReachedLimit()
		{
			return this.maximumStock.size() >= this.getKindsLimit();
		}

		public int getKindsLimit()
		{
			var building = this.buildingView;
			var increase = 1.0D + building.getColony().getResearchManager().getResearchEffects().getEffectStrength(ResearchConstants.MINIMUM_STOCK);
			return (int) (building.getBuildingLevel() * increase * MCTweaksConfigServer.INSTANCE.jobs.maximumStockKindsPerLevel.get());
		}

		public boolean contains(ItemStack stack)
		{
			return this.maximumStock.containsKey(new ItemStorage(stack));
		}

		public void add(ItemStack stack, int quantity)
		{
			if (stack.isEmpty())
			{
				return;
			}

			this.maximumStock.put(new ItemStorage(stack.copy()), quantity);
			MineColoniesTweaks.network().sendToServer(MaximumStockUpdateMessage.add(this, stack, quantity));
		}

		public void remove(ItemStack stack)
		{
			this.maximumStock.removeInt(new ItemStorage(stack));
			MineColoniesTweaks.network().sendToServer(MaximumStockUpdateMessage.remove(this, stack));
		}

		@Override
		public BOWindow getWindow()
		{
			return new MaximumStockModuleWindow(this.buildingView, this);
		}

		@Override
		public String getIcon()
		{
			return "maximumstock";
		}

		@Override
		public String getDesc()
		{
			return "com.minecolonies.coremod.gui.workerhuts." + this.getIcon();
		}

	}

	public static record Entry(ItemStack stack, int quantity)
	{

	}

}
