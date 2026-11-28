package steve_gall.minecolonies_tweaks.core.common.network.message;

import com.minecolonies.api.colony.buildings.views.IBuildingView;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.api.common.building.BuildingPos;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class BuildingRequestBuildMessage extends AbstractMessage
{
	public static final CustomPacketPayload.Type<BuildingRequestBuildMessage> TYPE = new CustomPacketPayload.Type<>(MineColoniesTweaks.rl("building_request_build"));

	private final BuildingPos buildingPos;
	private final BlockPos builderPos;
	private final BuildOption option;

	public BuildingRequestBuildMessage(IBuildingView building, BlockPos builderPos, BuildOption option)
	{
		this.buildingPos = new BuildingPos(building);
		this.builderPos = builderPos;
		this.option = option;
	}

	public BuildingRequestBuildMessage(RegistryFriendlyByteBuf buffer)
	{
		super(buffer);

		this.buildingPos = new BuildingPos(buffer);
		this.builderPos = buffer.readBlockPos();
		this.option = buffer.readEnum(BuildOption.class);
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buffer)
	{
		super.encode(buffer);

		this.buildingPos.serializeBuffer(buffer);
		buffer.writeBlockPos(this.builderPos);
		buffer.writeEnum(this.option);
	}

	@Override
	public void handle(IPayloadContext context)
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
				building.requestUpgrade(context.player(), this.builderPos);
			}

		}

	}

	@Override
	public CustomPacketPayload.Type<BuildingRequestBuildMessage> type()
	{
		return TYPE;
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
