package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.ScrollingList.DataProvider;
import com.minecolonies.api.util.constant.WindowConstants;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElement;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElementEvent;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class ResourceScrollBookListWindow extends AbstractWindowSkeleton
{
	private final List<ResourceScrollBookElement> elements;
	private final ScrollingList resourceList;

	public ResourceScrollBookListWindow(List<ItemStack> stacks, @Nullable BOWindow parent)
	{
		super(MineColoniesTweaks.rl("gui/resourcescroll_book_list_window.xml").toString(), null);

		this.elements = new ArrayList<>();

		for (var stack : stacks)
		{
			if (stack.isEmpty())
			{
				continue;
			}

			MinecraftForge.EVENT_BUS.post(new ResourceScrollBookElementEvent(stack, this.elements::add));
		}

		this.resourceList = this.findPaneOfTypeByID(WindowConstants.LIST_RESOURCES, ScrollingList.class);
		this.resourceList.setDataProvider(new DataProvider()
		{
			@Override
			public void updateElement(int index, Pane rowPane)
			{
				elements.get(index).update(index, rowPane);
			}

			@Override
			public int getElementCount()
			{
				return elements.size();
			}
		});
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		for (var element : this.elements)
		{
			element.update();
		}

	}

	@Override
	public void onButtonClicked(@NotNull Button button)
	{
		super.onButtonClicked(button);

		if (button.getID().equals(ResourceScrollBookElement.BUTTON_OPEN))
		{
			var index = this.resourceList.getListElementIndexByPane(button);
			var element = this.elements.get(index);
			element.onOpenClicked();
		}

	}

	@Override
	public boolean onKeyTyped(char ch, int key)
	{
		var index = -1;

		if (InputConstants.KEY_0 == key)
		{
			index = 8;
		}
		else if (InputConstants.KEY_1 <= key && key <= InputConstants.KEY_9)
		{
			index = key - InputConstants.KEY_1;
		}

		if (index > -1 && index < this.elements.size())
		{
			this.elements.get(index).onOpenClicked();
		}

		return super.onKeyTyped(ch, key);
	}

}
