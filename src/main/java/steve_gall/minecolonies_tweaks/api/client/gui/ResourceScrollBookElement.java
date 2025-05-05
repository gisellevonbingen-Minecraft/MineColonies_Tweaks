package steve_gall.minecolonies_tweaks.api.client.gui;

import org.jetbrains.annotations.NotNull;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.controls.Tooltip;
import com.minecolonies.api.util.constant.WindowConstants;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ResourceScrollBookElement
{
	public static final Component EMPTY = Component.empty();
	public static final Component BUILDER_NOT_SETTED = Component.translatable("minecolonies_tweaks.gui.resourcebook.builder_not_setted");
	public static final Component COLONY_NOT_SETTED = Component.translatable("minecolonies_tweaks.gui.resourcebook.colony_not_setted");

	public static final String BUTTON_OPEN = "open";
	public static final String LABEL_DESC1 = "desc1";
	public static final String LABEL_DESC2 = "desc2";
	public static final String LABEL_DESC3 = "desc3";

	@NotNull
	public final ItemStack stack;

	protected boolean valid = false;

	public ResourceScrollBookElement(@NotNull ItemStack stack)
	{
		this.stack = stack;
	}

	public void onOpenClicked()
	{

	}

	public void update()
	{
		this.valid = false;
	}

	public void update(int index, @NotNull Pane rowPane)
	{
		this.getResourceIcon(rowPane).setItem(this.stack);
		this.getDesc1Label(rowPane).setText(EMPTY);
		this.getDesc2Label(rowPane).setText(EMPTY);
		this.getDesc3Label(rowPane).setText(EMPTY);
		this.getTooltip1(rowPane).setText(EMPTY);
		this.getTooltip2(rowPane).setText(EMPTY);
		this.getTooltip3(rowPane).setText(EMPTY);
		this.getOpenButton(rowPane).setVisible(this.valid);
	}

	@NotNull
	public ItemIcon getResourceIcon(@NotNull Pane rowPane)
	{
		return rowPane.findPaneOfTypeByID(WindowConstants.RESOURCE_ICON, ItemIcon.class);
	}

	@NotNull
	public Text getDesc1Label(@NotNull Pane rowPane)
	{
		return rowPane.findPaneOfTypeByID(LABEL_DESC1, Text.class);
	}

	@NotNull
	public Text getDesc2Label(@NotNull Pane rowPane)
	{
		return rowPane.findPaneOfTypeByID(LABEL_DESC2, Text.class);
	}

	@NotNull
	public Text getDesc3Label(@NotNull Pane rowPane)
	{
		return rowPane.findPaneOfTypeByID(LABEL_DESC3, Text.class);
	}

	@NotNull
	public Button getOpenButton(@NotNull Pane rowPane)
	{
		return rowPane.findPaneOfTypeByID(BUTTON_OPEN, Button.class);
	}

	@NotNull
	public Tooltip getTooltip1(@NotNull Pane rowPane)
	{
		return this.getTooltip(this.getDesc1Label(rowPane));
	}

	@NotNull
	public Tooltip getTooltip2(@NotNull Pane rowPane)
	{
		return this.getTooltip(this.getDesc2Label(rowPane));
	}

	@NotNull
	public Tooltip getTooltip3(@NotNull Pane rowPane)
	{
		return this.getTooltip(this.getDesc3Label(rowPane));
	}

	@NotNull
	public Tooltip getTooltip(@NotNull Text label)
	{
		var hoverPane = label.getHoverPane();

		if (hoverPane instanceof Tooltip tooltip)
		{
			return tooltip;
		}
		else
		{
			return PaneBuilders.tooltipBuilder().hoverPane(label).build();
		}

	}

}
