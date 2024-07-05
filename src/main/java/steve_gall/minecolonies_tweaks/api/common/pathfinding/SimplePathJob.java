package steve_gall.minecolonies_tweaks.api.common.pathfinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.core.entity.pathfinding.MNode;
import com.minecolonies.core.entity.pathfinding.SurfaceType;
import com.minecolonies.core.entity.pathfinding.pathjobs.AbstractPathJob;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;

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
		super(level, start, range, result, entity);

		this.restrictionBox = null;
		this.home = home;
	}

	public SimplePathJob(@NotNull Level level, @NotNull BlockPos start, @NotNull BoundingBox restrictionBox, @Nullable Mob entity, RESULT result)
	{
		super(level, start, (int) getRange(start, restrictionBox), result, entity);

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
	protected double computeHeuristic(int x, int y, int z)
	{
		return distManhattan(this.home, x, y, z);
	}

	@Override
	protected boolean isAtDestination(@NotNull MNode n)
	{
		return n.parent != null && this.isNearTarget(n) && SurfaceType.getSurfaceType(this.world, this.cachedBlockLookup.getBlockState(n.x, n.y - 1, n.z), this.tempWorldPos.set(n.x, n.y - 1, n.z), getPathingOptions()) == SurfaceType.WALKABLE;
	}

	private boolean isNearTarget(@NotNull MNode n)
	{
		if (n.parent == null)
		{
			return false;
		}

		if (n.x == n.parent.x)
		{
			var dz = n.z > n.parent.z ? 1 : -1;
			return this.isTarget(n.x, n.y, n.z + dz) || this.isTarget(n.x - 1, n.y, n.z) || this.isTarget(n.x + 1, n.y, n.z);
		}
		else
		{
			var dx = n.x > n.parent.x ? 1 : -1;
			return this.isTarget(n.x + dx, n.y, n.z) || this.isTarget(n.x, n.y, n.z - 1) || this.isTarget(n.x, n.y, n.z + 1);
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
	protected double getEndNodeScore(@NotNull MNode n)
	{
		return 0.0D;
	}

	protected abstract boolean testPos(@NotNull MutableBlockPos pos);
}
