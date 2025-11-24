package steve_gall.minecolonies_tweaks.core.common.network.message;

import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.core.colony.buildingextensions.FarmField;
import com.minecolonies.core.tileentities.TileEntityScarecrow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import steve_gall.minecolonies_tweaks.api.common.network.AbstractMessage;

public class FarmFieldPlotResize2Message extends AbstractMessage
{
	private final int size;
	private final Direction direction;
	private final BlockPos position;

	public FarmFieldPlotResize2Message(int size, Direction direction, BlockPos position)
	{
		this.size = size;
		this.direction = direction;
		this.position = position;
	}

	public FarmFieldPlotResize2Message(FriendlyByteBuf buffer)
	{
		super(buffer);

		this.size = buffer.readInt();
		this.direction = Direction.from2DDataValue(buffer.readInt());
		this.position = buffer.readBlockPos();
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		super.encode(buffer);

		buffer.writeInt(this.size);
		buffer.writeInt(this.direction.get2DDataValue());
		buffer.writeBlockPos(this.position);
	}

	@Override
	public void handle(NetworkEvent.Context context)
	{
		super.handle(context);

		if (context.getSender().level().getBlockEntity(this.position) instanceof TileEntityScarecrow scarecrow)
		{
			scarecrow.setFieldSize(this.direction, this.size);

			var colony = scarecrow.getCurrentColony();

			if (colony != null)
			{
				colony.getBuildingManager().getMatchingBuildingExtension(f -> f.getBuildingExtensionType().equals(BuildingExtensionRegistries.farmField.get()) && f.getPosition().equals(this.position)).map(m -> (FarmField) m).ifPresent(field -> field.setRadius(this.direction, this.size));
			}

		}

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
