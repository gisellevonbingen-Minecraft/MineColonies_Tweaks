package steve_gall.minecolonies_tweaks.core.client.gui;

import com.ldtteam.blockui.Pane;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.items.component.ColonyId;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElement;
import steve_gall.minecolonies_tweaks.mixin.common.minecolonies.ItemQuestLogAccessor;

public class QuestLogElement extends ResourceScrollBookElement
{
	private Component text1 = EMPTY;

	public QuestLogElement(ItemStack stack)
	{
		super(stack);
	}

	@Override
	public void onOpenClicked()
	{
		super.onOpenClicked();

		var mc = Minecraft.getInstance();
		ItemQuestLogAccessor.invokeOpenWindow(this.stack, mc.level, mc.player);
	}

	@Override
	public void update()
	{
		super.update();

		var colonyView = this.getColonyView();

		if (colonyView == null)
		{
			this.text1 = COLONY_NOT_SETTED;
			return;
		}

		this.valid = true;
		this.text1 = Component.literal(colonyView.getName()).withStyle(ChatFormatting.DARK_PURPLE);
	}

	@Override
	public void update(int index, Pane rowPane)
	{
		super.update(index, rowPane);

		this.getDesc1Label(rowPane).setText(this.stack.getHoverName());
		this.getDesc2Label(rowPane).setText(this.text1);
	}

	public IColonyView getColonyView()
	{
		return ColonyId.readColonyViewFromItemStack(this.stack);
	}

}
