package steve_gall.minecolonies_tweaks.core.common.network.message;

import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.core.colony.buildingextensions.FarmField;
import com.minecolonies.core.tileentities.TileEntityScarecrow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;

public class FarmFieldPlotResize2Message extends AbstractMessage
{
	public static final CustomPacketPayload.Type<FarmFieldPlotResize2Message> TYPE = new CustomPacketPayload.Type<>(MineColoniesTweaks.rl("farm_field_plot_resize"));

	private final int size;
	private final Direction direction;
	private final BlockPos position;

	public FarmFieldPlotResize2Message(int size, Direction direction, BlockPos position)
	{
		this.size = size;
		this.direction = direction;
		this.position = position;
	}

	public FarmFieldPlotResize2Message(RegistryFriendlyByteBuf buffer)
	{
		super(buffer);

		this.size = buffer.readInt();
		this.direction = Direction.from2DDataValue(buffer.readInt());
		this.position = buffer.readBlockPos();
	}

	@Override
	public void encode(RegistryFriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeInt(this.size);
		buffer.writeInt(this.direction.get2DDataValue());
		buffer.writeBlockPos(this.position);
	}

	@Override
	public void handle(IPayloadContext context)
	{
		super.handle(context);

		if (context.player().level().getBlockEntity(this.position) instanceof TileEntityScarecrow scarecrow)
		{
			scarecrow.setFieldSize(this.direction, this.size);

			var colony = scarecrow.getCurrentColony();

			if (colony != null)
			{
				colony.getServerBuildingManager().getMatchingBuildingExtension(f -> f.getBuildingExtensionType().equals(BuildingExtensionRegistries.farmField.get()) && f.getPosition().equals(this.position)).map(m -> (FarmField) m).ifPresent(field -> field.setRadius(this.direction, this.size));
			}

		}

	}

	@Override
	public CustomPacketPayload.Type<FarmFieldPlotResize2Message> type()
	{
		return TYPE;
	}

	public int getSize()
	{
		return this.size;
	}

	public Direction getDirection()
	{
		return this.direction;
	}

	public BlockPos getPosition()
	{
		return this.position;
	}

}
