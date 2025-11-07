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

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElement;
import steve_gall.minecolonies_tweaks.api.client.gui.ResourceScrollBookElementEvent;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.init.MCTweaksTags;

public class ResourceScrollBookListWindow extends AbstractWindowSkeleton
{
	public static final Component EMPTY = Component.empty();
	public static final Component BUILDER_NOT_SETTED = Component.translatable("minecolonies_tweaks.gui.resourcebook.builder_not_setted");
	public static final Component COLONY_NOT_SETTED = Component.translatable("minecolonies_tweaks.gui.resourcebook.colony_not_setted");

	public static final String BUTTON_OPEN = "open";
	public static final String LABEL_DESC1 = "desc1";
	public static final String LABEL_DESC2 = "desc2";

	private final List<ResourceScrollBookElement> elements;
	private final ScrollingList resourceList;

	public ResourceScrollBookListWindow(@Nullable BOWindow parent, List<ItemStack> stacks)
	{
		super(null, MineColoniesTweaks.rl("gui/resourcescroll_book_list_window.xml"));

		this.elements = new ArrayList<>();

		for (var stack : stacks)
		{
			if (stack.isEmpty())
			{
				continue;
			}
			else if (stack.is(MCTweaksTags.Items.RESOURCESCROLLBOOK_ELEMENT))
			{
				NeoForge.EVENT_BUS.post(new ResourceScrollBookElementEvent(stack, this.elements::add));
			}

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

		if (button.getID().equals(BUTTON_OPEN))
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
