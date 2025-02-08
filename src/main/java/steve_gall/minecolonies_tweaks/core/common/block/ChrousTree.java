package steve_gall.minecolonies_tweaks.core.common.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ChrousTree
{
	private final LevelReader level;
	private BlockPos log;
	private final Set<BlockPos> positions;
	private final List<BlockPos> aliveFlowers;
	private final List<BlockPos> deadFlowers;
	private final boolean isChorusTree;

	public ChrousTree(LevelReader level, BlockPos pos)
	{
		this.level = level;
		this.log = pos;
		this.positions = new HashSet<>();
		this.aliveFlowers = new ArrayList<>();
		this.deadFlowers = new ArrayList<>();

		var seekPositions = new HashSet<BlockPos>();
		seekPositions.add(pos);

		var state = level.getBlockState(pos);

		if (state.is(Blocks.CHORUS_PLANT))
		{
			this.positions.add(pos);
			this.discoverChorusPlant(pos, state, seekPositions);
			this.isChorusTree = true;
		}
		else if (state.is(Blocks.CHORUS_FLOWER))
		{
			this.positions.add(pos);
			this.discoverChorusFlower(pos, state, seekPositions);
			this.isChorusTree = true;
		}
		else
		{
			this.isChorusTree = false;
		}

	}

	private void discoverChorusPlant(BlockPos pos, BlockState state, Set<BlockPos> seekPositions)
	{
		for (var entry : PipeBlock.PROPERTY_BY_DIRECTION.entrySet())
		{
			if (state.getValue(entry.getValue()))
			{
				this.findFlower(pos.relative(entry.getKey()), seekPositions);
			}

		}

	}

	private void discoverChorusFlower(BlockPos pos, BlockState state, Set<BlockPos> seekPositions)
	{
		(state.getValue(ChorusFlowerBlock.AGE) == ChorusFlowerBlock.DEAD_AGE ? this.deadFlowers : this.aliveFlowers).add(pos);

		for (var direction : Direction.values())
		{
			if (direction == Direction.UP)
			{
				continue;
			}

			this.findFlower(pos.relative(direction), seekPositions);
		}

	}

	private void findFlower(BlockPos pos, Set<BlockPos> seekPositions)
	{
		if (seekPositions.contains(pos))
		{
			return;
		}

		seekPositions.add(pos);

		var state = this.level.getBlockState(pos);
		var isPlant = state.is(Blocks.CHORUS_PLANT);
		var isFlower = state.is(Blocks.CHORUS_FLOWER);

		if (isPlant || isFlower)
		{
			this.positions.add(pos);

			if (pos.getY() < this.log.getY())
			{
				this.log = pos;
			}

		}

		if (isPlant)
		{
			this.discoverChorusPlant(pos, state, seekPositions);
		}
		else if (isFlower)
		{
			(state.getValue(ChorusFlowerBlock.AGE) == ChorusFlowerBlock.DEAD_AGE ? this.deadFlowers : this.aliveFlowers).add(pos);
		}

	}

	public BlockPos getLog()
	{
		return this.log;
	}

	public boolean isChorusTree()
	{
		return this.isChorusTree;
	}

	public Set<BlockPos> getPositions()
	{
		return Collections.unmodifiableSet(this.positions);
	}

	public List<BlockPos> getAliveFlowers()
	{
		return Collections.unmodifiableList(this.aliveFlowers);
	}

	public List<BlockPos> getDeadFlowers()
	{
		return Collections.unmodifiableList(this.deadFlowers);
	}

}
