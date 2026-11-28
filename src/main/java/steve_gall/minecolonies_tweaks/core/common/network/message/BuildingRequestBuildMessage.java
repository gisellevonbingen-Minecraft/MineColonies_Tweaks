package steve_gall.minecolonies_tweaks.core.common.network.message;

import com.minecolonies.api.colony.buildings.views.IBuildingView;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.api.common.building.BuildingPos;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;

public class BuildingRequestBuildMessage extends AbstractMessage
{
	private final BuildingPos buildingPos;
	private final BlockPos builderPos;
	private final BuildOption option;

	public BuildingRequestBuildMessage(IBuildingView building, BlockPos builderPos, BuildOption option)
	{
		this.buildingPos = new BuildingPos(building);
		this.builderPos = builderPos;
		this.option = option;
	}

	public BuildingRequestBuildMessage(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.buildingPos = new BuildingPos(buffer);
		this.builderPos = buffer.readBlockPos();
		this.option = buffer.readEnum(BuildOption.class);
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		this.buildingPos.serializeBuffer(buffer);
		buffer.writeBlockPos(this.builderPos);
		buffer.writeEnum(this.option);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		super.handle(context);

		var building = this.buildingPos.getBuilding();

		if (building != null)
		{
			if (this.option == BuildOption.REPAIR)
			{
				building.requestRepair(this.builderPos);
			}
			else if (this.option == BuildOption.UPGRADE)
			{
				building.requestUpgrade(context.getSender(), this.builderPos);
			}

		}

	}

	public BuildingPos getModulePos()
	{
		return this.buildingPos;
	}

	public BlockPos getBuilderPos()
	{
		return this.builderPos;
	}

	public BuildOption getOption()
	{
		return this.option;
	}

	public static enum BuildOption
	{
		REPAIR,
		UPGRADE,
	}

}
