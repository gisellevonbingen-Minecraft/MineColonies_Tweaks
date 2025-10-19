package steve_gall.minecolonies_tweaks.core.common.building.module;

import com.ldtteam.blockui.views.BOWindow;

import steve_gall.minecolonies_tweaks.api.common.building.module.AbstractIdListModule;
import steve_gall.minecolonies_tweaks.api.common.building.module.AbstractIdListModuleView;
import steve_gall.minecolonies_tweaks.core.client.gui.StudyItemListModuleWindow;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class StudyItemListModule extends AbstractIdListModule
{
	public StudyItemListModule(String listId)
	{
		super(listId);
	}

	public static class View extends AbstractIdListModuleView
	{
		private final String icon;
		private final String desc;
		private final boolean inverted;

		public View(String icon, String desc, boolean inverted)
		{
			this.icon = icon;
			this.desc = desc;
			this.inverted = inverted;
		}

		@Override
		public BOWindow getWindow()
		{
			return new StudyItemListModuleWindow(MineColoniesTweaks.rl("gui/layouthuts/layoutfilterablestudyitemlist.xml").toString(), this);
		}

		@Override
		public String getIcon()
		{
			return this.icon;
		}

		@Override
		public String getDesc()
		{
			return this.desc;
		}

		public boolean isInverted()
		{
			return this.inverted;
		}

	}

}
