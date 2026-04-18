package steve_gall.minecolonies_tweaks.core.client.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.Image;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.controls.Tooltip;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.ScrollingList.DataProvider;
import com.minecolonies.api.util.constant.WindowConstants;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.item.ItemCopyScroll.ModuleViewInfo;
import steve_gall.minecolonies_tweaks.core.common.network.message.CopyScrollRemoveEntryMessage;

public class CopyScrollWindow extends AbstractWindowSkeleton
{
	public static final Component TEXT_SURELY = Component.translatable("minecolonies_tweaks.gui.surely");
	public static final Component TEXT_X = Component.literal("X");

	private final List<ModuleCache> moduleViewCacheList;
	private final InteractionHand hand;

	private final ScrollingList resourceList;

	private Button confirmButton;

	public CopyScrollWindow(Component buildingName, Collection<ModuleViewInfo> moduleViewInfoList, InteractionHand hand, @Nullable BOWindow parent)
	{
		super(null, MineColoniesTweaks.rl("gui/copyscroll_window.xml"));

		this.moduleViewCacheList = moduleViewInfoList.stream().map(ModuleCache::new).collect(Collectors.toList());
		this.hand = hand;

		this.findPaneOfTypeByID(WindowConstants.WORK_ORDER_NAME, Text.class).setText(buildingName);
		this.resourceList = this.findPaneOfTypeByID(WindowConstants.LIST_RESOURCES, ScrollingList.class);
		this.resourceList.setDataProvider(new DataProvider()
		{
			@Override
			public void updateElement(int index, Pane rowPane)
			{
				var cache = CopyScrollWindow.this.moduleViewCacheList.get(index);
				var iconImage = rowPane.findPaneOfTypeByID("icon", Image.class);
				iconImage.setImage(cache.icon, false);

				var textLabel = rowPane.findPaneOfTypeByID("text", Text.class);
				textLabel.setText(cache.info.text());

				if (textLabel.getHoverPane() instanceof Tooltip tooltip)
				{
					if (Screen.hasShiftDown())
					{
						tooltip.enable();
					}
					else
					{
						tooltip.disable();
					}

					tooltip.setText(cache.tooltip);
				}

			}

			@Override
			public int getElementCount()
			{
				return CopyScrollWindow.this.moduleViewCacheList.size();
			}
		});
	}

	@Override
	public void onButtonClicked(Button button)
	{
		super.onButtonClicked(button);

		var confirmButton = this.confirmButton;

		if (confirmButton != null)
		{
			confirmButton.setText(TEXT_X);
			this.confirmButton = null;
		}

		if (button.getID().endsWith("remove"))
		{
			if (confirmButton == button)
			{
				var row = this.resourceList.getListElementIndexByPane(button);
				var cache = this.moduleViewCacheList.get(row);

				this.moduleViewCacheList.remove(row);
				MineColoniesTweaks.network().sendToServer(new CopyScrollRemoveEntryMessage(cache.info.key(), this.hand));
			}
			else
			{
				this.confirmButton = button;
				this.confirmButton.setText(TEXT_SURELY);
			}

		}

	}

	public class ModuleCache
	{
		public final ModuleViewInfo info;
		public final ResourceLocation icon;
		public final List<MutableComponent> tooltip;

		public ModuleCache(ModuleViewInfo info)
		{
			this.info = info;
			this.icon = info.icon();
			this.tooltip = Arrays.asList(Component.literal(info.key()), Component.empty().append(info.text()));
		}

	}

}
