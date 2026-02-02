package steve_gall.minecolonies_tweaks.core.common.building;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.tileentities.AbstractTileEntityRack;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class BuildingEmptySlotCounter
{
	private static final DecimalFormat SLOTS_FORMAT = new DecimalFormat("#,###");
	private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.0");

	private static final int EMPTY_SLOTS_UPDATE_TICKS = 20;

	private final IBuildingView buildingView;

	private boolean first = true;
	private List<BlockPos> positions = null;
	private int ticks = 0;
	private int emptySlots = 0;
	private int totalSlots = 0;

	public BuildingEmptySlotCounter(IBuildingView buildingView)
	{
		this.buildingView = buildingView;
	}

	public boolean update()
	{
		if (this.first)
		{
			this.first = false;
			this.updatePositions();

			for (var position : this.positions)
			{
				this.countUp(position);
			}

			return true;
		}
		else
		{
			if (this.ticks == 0)
			{
				this.emptySlots = 0;
				this.totalSlots = 0;
				this.updatePositions();
			}

			var containerCount = this.positions.size();
			var count = (containerCount + EMPTY_SLOTS_UPDATE_TICKS - 1) / EMPTY_SLOTS_UPDATE_TICKS;
			var offset = this.ticks * count;
			var endIndex = Math.min(offset + count, containerCount);

			for (var i = offset; i < endIndex; i++)
			{
				this.countUp(this.positions.get(i));
			}

			this.ticks++;

			if (this.ticks >= EMPTY_SLOTS_UPDATE_TICKS)
			{
				this.ticks = 0;
				return true;
			}
			else
			{
				return false;
			}

		}

	}

	private void updatePositions()
	{
		var positions = new ArrayList<BlockPos>();
		positions.add(buildingView.getPosition());
		positions.addAll(buildingView.getContainers());
		this.positions = positions;
	}

	private void countUp(BlockPos position)
	{
		var level = this.buildingView.getColony().getWorld();
		var blockEntity = level.getBlockEntity(position);
		var itemHandler = this.getItemHandler(blockEntity);

		if (itemHandler == null)
		{
			return;
		}

		var slots = itemHandler.getSlots();

		for (var i = 0; i < slots; i++)
		{
			if (itemHandler.getStackInSlot(i).isEmpty())
			{
				this.emptySlots++;
			}

			this.totalSlots++;
		}

	}

	private IItemHandler getItemHandler(BlockEntity blockEntity)
	{
		if (blockEntity == null)
		{
			return null;
		}
		else if (blockEntity instanceof AbstractTileEntityRack rack)
		{
			return rack.getInventory();
		}
		else
		{
			var cap = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
			return cap == null ? null : cap.orElse(null);
		}

	}

	public IBuildingView getBuildingView()
	{
		return this.buildingView;
	}

	public int getEmptySlots()
	{
		return this.emptySlots;
	}

	public int getTotalSlots()
	{
		return this.totalSlots;
	}

	public Component getSlotsText()
	{
		var s1 = SLOTS_FORMAT.format(this.emptySlots);
		var s2 = SLOTS_FORMAT.format(this.totalSlots);
		return Component.translatable("minecolonies_tweaks.gui.warehouse.empty_slots", s1, s2);
	}

	public String getPercentText()
	{
		return PERCENT_FORMAT.format(this.emptySlots / (this.totalSlots / 100.0F)) + "%";
	}

}
