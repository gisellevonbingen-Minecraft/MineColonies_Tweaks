package steve_gall.minecolonies_tweaks.core.client.gui;

import com.ldtteam.blockui.Pane;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.core.colony.buildings.moduleviews.BuildingResourcesModuleView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElement;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.ItemResourceScrollAccessor;

public class ResourceScrollElement extends ResourceScrollBookElement
{
	private Component workerName = EMPTY;
	private Component constructionName = EMPTY;
	private Component progress = EMPTY;

	public ResourceScrollElement(ItemStack stack)
	{
		super(stack);
	}

	@Override
	public void onOpenClicked()
	{
		super.onOpenClicked();

		var compound = this.stack.getOrCreateTag();
		var mc = Minecraft.getInstance();
		ItemResourceScrollAccessor.invokeOpenWindow(compound, mc.player);
	}

	@Override
	public void update()
	{
		super.update();

		var buildingView = this.getBuildingView();

		if (buildingView == null)
		{
			this.workerName = EMPTY;
			this.constructionName = BUILDER_NOT_SETTED;
			this.progress = EMPTY;
			return;
		}

		this.valid = true;
		this.workerName = Component.literal(buildingView.getWorkerName()).withStyle(ChatFormatting.DARK_PURPLE);

		var resourceView = buildingView.getModuleViewByType(BuildingResourcesModuleView.class);
		var workOrderView = buildingView.getColony().getWorkOrder(resourceView.getWorkOrderId());

		if (workOrderView != null)
		{
			this.constructionName = Component.literal(workOrderView.getDisplayName().getString().replace("\n", " "));
		}
		else
		{
			this.constructionName = Component.empty();
		}

		double supplied = 0.0D;
		double total = 0.0D;

		for (var resource : resourceView.getResources().values())
		{
			supplied += Math.min(resource.getAvailable(), resource.getAmount());
			total += resource.getAmount();
		}

		if (total > 0.0D)
		{
			this.progress = Component.translatable("com.minecolonies.coremod.gui.progress.res", (int) ((supplied / total) * 100) + "%", resourceView.getProgress() + "%");
		}
		else
		{
			this.progress = EMPTY;
		}

	}

	@Override
	public void update(int index, Pane rowPane)
	{
		super.update(index, rowPane);

		this.getDesc1Label(rowPane).setText(this.workerName);
		this.getDesc2Label(rowPane).setText(this.constructionName);
		this.getDesc3Label(rowPane).setText(this.progress);
		this.getTooltip2(rowPane).setText(this.constructionName);
	}

	public BuildingBuilder.View getBuildingView()
	{
		var compound = this.stack.getTag();

		if (compound != null)
		{
			var colonyId = compound.getInt(NbtTagConstants.TAG_COLONY_ID);
			var builderPos = compound.contains(NbtTagConstants.TAG_BUILDER) ? BlockPosUtil.read(compound, NbtTagConstants.TAG_BUILDER) : null;

			if (builderPos != null)
			{
				var mc = Minecraft.getInstance();
				var colonyView = IColonyManager.getInstance().getColonyView(colonyId, mc.level.dimension());

				if (colonyView != null && colonyView.getBuilding(builderPos) instanceof BuildingBuilder.View buildingView)
				{
					return buildingView;
				}

			}

		}

		return null;
	}

}
