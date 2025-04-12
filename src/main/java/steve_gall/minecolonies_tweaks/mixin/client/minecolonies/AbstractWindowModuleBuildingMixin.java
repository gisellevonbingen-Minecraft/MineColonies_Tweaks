package steve_gall.minecolonies_tweaks.mixin.client.minecolonies;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.controls.Text;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.tileentities.AbstractTileEntityRack;
import com.minecolonies.core.client.gui.AbstractModuleWindow;
import com.minecolonies.core.client.gui.AbstractWindowModuleBuilding;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingWareHouse;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

@Mixin(value = AbstractWindowModuleBuilding.class, remap = false)
public abstract class AbstractWindowModuleBuildingMixin<B extends IBuildingView> extends AbstractModuleWindow
{
	private static final String TEXT_EMPTY_SLOTS = "empty_slots";
	private static final DecimalFormat SLOTS_FORMAT = new DecimalFormat("#,###");
	private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.0");

	private static final int EMPTY_SLOTS_UPDATE_TICKS = 20;

	@Unique
	private boolean minecolonies_tweaks$first = true;
	@Unique
	private int minecolonies_tweaks$updateTicks;
	@Unique
	private List<BlockPos> minecolonies_tweaks$containerPositions;
	@Unique
	private int minecolonies_tweaks$emptySlots;
	@Unique
	private int minecolonies_tweaks$totalSlots;

	public AbstractWindowModuleBuildingMixin(IBuildingView building, String res)
	{
		super(building, res);
	}

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void init(B building, String resource, CallbackInfo ci)
	{
		if (building instanceof BuildingWareHouse.View)
		{
			Loader.createFromXMLFile(MineColoniesTweaks.rl("gui/layouthuts/layoutwarehouse.xml"), this);
		}

	}

	@Inject(method = "onUpdate", remap = false, at = @At(value = "TAIL"), cancellable = false)
	private void onUpdate(CallbackInfo ci)
	{
		if (this.buildingView instanceof BuildingWareHouse.View)
		{
			if (this.minecolonies_tweaks$first)
			{
				this.minecolonies_tweaks$first = false;

				for (var position : this.getContainerPositions())
				{
					this.countUp(position);
				}

				this.updateText();
			}
			else
			{
				if (this.minecolonies_tweaks$updateTicks == 0)
				{
					this.minecolonies_tweaks$emptySlots = 0;
					this.minecolonies_tweaks$totalSlots = 0;
					this.minecolonies_tweaks$containerPositions = this.getContainerPositions();
				}

				var containerCount = this.minecolonies_tweaks$containerPositions.size();
				var count = (containerCount + EMPTY_SLOTS_UPDATE_TICKS - 1) / EMPTY_SLOTS_UPDATE_TICKS;
				var offset = this.minecolonies_tweaks$updateTicks * count;
				var endIndex = Math.min(offset + count, containerCount);

				for (var i = offset; i < endIndex; i++)
				{
					this.countUp(this.minecolonies_tweaks$containerPositions.get(i));
				}

				this.minecolonies_tweaks$updateTicks++;

				if (this.minecolonies_tweaks$updateTicks >= EMPTY_SLOTS_UPDATE_TICKS)
				{
					this.updateText();
					this.minecolonies_tweaks$updateTicks = 0;
				}

			}

		}

	}

	private void updateText()
	{
		var text = this.findPaneOfTypeByID(TEXT_EMPTY_SLOTS, Text.class);
		var s1 = SLOTS_FORMAT.format(this.minecolonies_tweaks$emptySlots);
		var s2 = SLOTS_FORMAT.format(this.minecolonies_tweaks$totalSlots);
		var s3 = PERCENT_FORMAT.format(this.minecolonies_tweaks$emptySlots / (this.minecolonies_tweaks$totalSlots / 100.0F)) + "%";
		text.setText(Component.translatable("minecolonies_tweaks.gui.warehouse.empty_slots", s1, s2, s3));
	}

	private List<BlockPos> getContainerPositions()
	{
		var positions = new ArrayList<BlockPos>();
		positions.add(this.buildingView.getPosition());
		positions.addAll(this.buildingView.getContainerList());
		return positions;
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
				this.minecolonies_tweaks$emptySlots++;
			}

			this.minecolonies_tweaks$totalSlots++;
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

}
