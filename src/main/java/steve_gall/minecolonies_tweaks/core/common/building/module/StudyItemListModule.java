package steve_gall.minecolonies_tweaks.core.common.building.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.ldtteam.blockui.views.BOWindow;
import com.minecolonies.api.entity.ai.util.StudyItem;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingLibrary;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;
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

	@Override
	public void serializeToView(@NotNull FriendlyByteBuf buf)
	{
		super.serializeToView(buf);

		if (this.building instanceof BuildingLibrary library)
		{
			buf.writeCollection(library.getStudyItems(), (b, i) ->
			{
				b.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, i.getItem());
				b.writeInt(i.getSkillIncreasePct());
				b.writeInt(i.getBreakPct());
			});
		}

	}

	public static class View extends AbstractIdListModuleView
	{
		private final String icon;
		private final String desc;
		private final boolean inverted;

		private final List<StudyItem> studyItems;

		public View(String icon, String desc, boolean inverted)
		{
			this.icon = icon;
			this.desc = desc;
			this.inverted = inverted;

			this.studyItems = new ArrayList<StudyItem>();
		}

		@Override
		public void deserialize(@NotNull FriendlyByteBuf buf)
		{
			super.deserialize(buf);

			this.studyItems.clear();
			this.studyItems.addAll(buf.readCollection(ArrayList::new, b ->
			{
				var item = b.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
				var skillIncrease = b.readInt();
				var breakChance = b.readInt();
				return new StudyItem(item, skillIncrease, breakChance);
			}));
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

		public List<StudyItem> getStudyItems()
		{
			return Collections.unmodifiableList(this.studyItems);
		}

	}

}
