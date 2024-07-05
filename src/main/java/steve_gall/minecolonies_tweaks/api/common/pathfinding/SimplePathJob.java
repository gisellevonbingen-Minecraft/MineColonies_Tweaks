package steve_gall.minecolonies_tweaks.api.common.pathfinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.entity.pathfinding.PathResult;
import com.minecolonies.core.entity.pathfinding.MNode;
import com.minecolonies.core.entity.pathfinding.pathjobs.AbstractPathJob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

@SuppressWarnings("rawtypes")
public abstract class SimplePathJob<RESULT extends PathResult> extends AbstractPathJob
{
	@NotNull
	private final BlockPos home;
	@Nullable
	private final BoundingBox restrictionBox;
	@NotNull
	private final MutableBlockPos temp = new MutableBlockPos();

	public SimplePathJob(@NotNull Level level, @NotNull BlockPos start, @NotNull BlockPos home, int range, @Nullable Mob entity, @NotNull RESULT result)
	{
		super(level, start, start, range, result, entity);

		this.restrictionBox = null;
		this.home = home;
	}

	public SimplePathJob(@NotNull Level level, @NotNull BlockPos start, @NotNull BoundingBox restrictionBox, @Nullable Mob entity, RESULT result)
	{
		super(level, start, start, (int) getRange(start, restrictionBox), result, entity);

		this.restrictionBox = restrictionBox;
		this.home = restrictionBox.getCenter();
	}

	private static double getRange(@NotNull BlockPos home, @NotNull BoundingBox restrictionBox)
	{
		int minX = restrictionBox.minX();
		int minY = restrictionBox.minY();
		int minZ = restrictionBox.minZ();
		int maxX = restrictionBox.maxX();
		int maxY = restrictionBox.maxY();
		int maxZ = restrictionBox.maxZ();
		return Math.max(distManhattan(home, minX, minY, minZ), distManhattan(home, maxX, maxY, maxZ)) + distManhattan(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static int distManhattan(BlockPos pos, BlockPos pos2)
	{
		return distManhattan(pos, pos2.getX(), pos2.getY(), pos2.getZ());
	}

	public static int distManhattan(BlockPos pos, int x2, int y2, int z2)
	{
		return distManhattan(pos.getX(), pos.getY(), pos.getZ(), x2, y2, z2);
	}

	public static int distManhattan(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		var xDist = Math.abs(x1 - x2);
		var yDist = Math.abs(y1 - y2);
		var zDist = Math.abs(z1 - z2);
		return xDist + yDist + zDist;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	public RESULT getResult()
	{
		return (RESULT) super.getResult();
	}

	@Override
	protected double computeHeuristic(@NotNull BlockPos pos)
	{
		return distManhattan(this.home, pos);
	}

	@Override
	protected boolean isAtDestination(@NotNull MNode n)
	{
		return n.parent != null && this.isNearTarget(n);
	}

	private boolean isNearTarget(@NotNull MNode n)
	{
		var pPos = n.parent.pos;
		var nx = n.pos.getX();
		var ny = n.pos.getY();
		var nz = n.pos.getZ();

		if (nx == pPos.getX())
		{
			var dz = nz > pPos.getZ() ? 1 : -1;
			return this.isTarget(nx, ny, nz + dz) || this.isTarget(nx - 1, ny, nz) || this.isTarget(nx + 1, ny, nz);
		}
		else
		{
			var dx = nx > pPos.getX() ? 1 : -1;
			return this.isTarget(nx + dx, ny, nz) || this.isTarget(nx, ny, nz - 1) || this.isTarget(nx, ny, nz + 1);
		}

	}

	private boolean isTarget(int x, int y, int z)
	{
		return this.isTarget(this.temp.set(x, y, z));
	}

	protected boolean isTarget(@NotNull MutableBlockPos pos)
	{
		return this.testRestrictionBox(pos) && this.testPos(pos);
	}

	protected boolean testRestrictionBox(@NotNull MutableBlockPos pos)
	{
		if (this.restrictionBox == null)
		{
			return true;
		}

		return this.restrictionBox.minX() <= pos.getX() && pos.getX() <= this.restrictionBox.maxX() && //
				this.restrictionBox.minY() <= pos.getY() && pos.getY() <= this.restrictionBox.maxY() && //
				this.restrictionBox.minZ() <= pos.getZ() && pos.getZ() <= this.restrictionBox.maxZ();
	}

	@Override
	protected double getNodeResultScore(@NotNull MNode n)
	{
		return 0.0D;
	}

	protected abstract boolean testPos(@NotNull MutableBlockPos pos);
}
