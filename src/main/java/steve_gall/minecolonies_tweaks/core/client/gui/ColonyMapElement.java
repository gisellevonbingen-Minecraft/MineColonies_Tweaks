package steve_gall.minecolonies_tweaks.core.client.gui;

import com.ldtteam.blockui.Pane;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.core.items.ItemClipboard;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElement;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.ItemColonyMapAccessor;

public class ColonyMapElement extends ResourceScrollBookElement
{
	private Component colonyName = EMPTY;

	public ColonyMapElement(ItemStack stack)
	{
		super(stack);
	}

	@Override
	public void onOpenClicked()
	{
		super.onOpenClicked();

		var compound = this.stack.getOrCreateTag();
		var mc = Minecraft.getInstance();
		ItemColonyMapAccessor.invokeOpenWindow(compound, mc.level, mc.player);
	}

	@Override
	public void update()
	{
		super.update();

		var colonyView = this.getColonyView();

		if (colonyView == null)
		{
			this.colonyName = EMPTY;
			return;
		}

		this.valid = true;
		this.colonyName = Component.literal(colonyView.getName()).withStyle(ChatFormatting.DARK_PURPLE);
	}

	@Override
	public void update(int index, Pane rowPane)
	{
		super.update(index, rowPane);

		this.getWorkerNameLabel(rowPane).setText(this.colonyName);
	}

	public IColonyView getColonyView()
	{
		var compound = this.stack.getTag();

		if (compound != null && compound.contains(ItemClipboard.TAG_COLONY))
		{
			var colonyId = compound.getInt(ItemClipboard.TAG_COLONY);
			var mc = Minecraft.getInstance();
			return IColonyManager.getInstance().getColonyView(colonyId, mc.level.dimension());
		}

		return null;
	}

}
