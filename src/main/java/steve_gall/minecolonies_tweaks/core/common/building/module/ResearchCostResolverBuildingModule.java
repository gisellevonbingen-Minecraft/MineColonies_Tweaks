package steve_gall.minecolonies_tweaks.core.common.building.module;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.ldtteam.blockui.views.BOWindow;
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.colony.buildings.modules.ICreatesResolversModule;
import com.minecolonies.api.colony.requestsystem.resolver.IRequestResolver;
import com.minecolonies.api.util.constant.TypeConstants;

import net.minecraft.network.FriendlyByteBuf;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchCostResolver;

public class ResearchCostResolverBuildingModule extends AbstractBuildingModule implements ICreatesResolversModule
{
	@Override
	public List<IRequestResolver<?>> createResolvers()
	{
		var location = this.getBuilding().getLocation();
		return Arrays.asList(new ResearchCostResolver(location, this.getBuilding().getColony().getRequestManager().getFactoryController().getNewInstance(TypeConstants.ITOKEN)));
	}

	@Override
	public void serializeToView(FriendlyByteBuf buf)
	{
		super.serializeToView(buf);
	}

	public static class View extends AbstractBuildingModuleView
	{
		@Override
		public void deserialize(@NotNull FriendlyByteBuf buf)
		{

		}

		@Override
		public boolean isPageVisible()
		{
			return false;
		}

		@Override
		public BOWindow getWindow()
		{
			return null;
		}

		@Override
		public String getIcon()
		{
			return null;
		}

		@Override
		public String getDesc()
		{
			return null;
		}

	}

}
