package steve_gall.minecolonies_tweaks.api.client.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.RandomState;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigCommon;

public class Test
{

	public static boolean test(RandomState randomState, ResourceLocation randomKey, ChunkPos chunkPos)
	{
		var chance = MCTweaksConfigCommon.INSTANCE.worldGens.emptyColoniesGenerationChance.get();

		if (chance <= 0.0D)
		{
			return false;
		}
		else if (chance >= 1.0D)
		{
			return true;
		}
		else
		{
			var chunkBlockPos = chunkPos.getBlockAt(0, 0, 0);
			var random = randomState.getOrCreateRandomFactory(randomKey).at(chunkBlockPos);
			var next = random.nextDouble();

			return 0 <= next && next <= chance;
		}

	}
}
