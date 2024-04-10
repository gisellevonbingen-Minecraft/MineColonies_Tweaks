package steve_gall.minecolonies_tweaks.core.common.mixin.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minecolonies.core.structures.EmptyColonyStructure;
import com.minecolonies.core.structures.EmptyNetherColonyStructure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import steve_gall.minecolonies_tweaks.core.common.MineColoniesTweaks;
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigCommon;

@Mixin(value = ChunkGenerator.class)
public class ChunkGeneratorMixin
{
	@Inject(method = "tryGenerateStructure", at = @At(value = "HEAD"), cancellable = true)
	private void tryGenerateStructure(StructureSet.StructureSelectionEntry entry, StructureManager structureManager, RegistryAccess registryAccess, RandomState randomState, StructureTemplateManager structureTemplateManager, long p_223110_, ChunkAccess chunkAccess, ChunkPos chunkPos, SectionPos sectionPos, CallbackInfoReturnable<Boolean> cir)
	{
		Structure structure = entry.structure().value();

		if (structure instanceof EmptyColonyStructure)
		{
			this.testAndCancel(randomState, MineColoniesTweaks.rl("world_gen/empty_colony"), chunkPos, cir);
		}
		else if (structure instanceof EmptyNetherColonyStructure)
		{
			this.testAndCancel(randomState, MineColoniesTweaks.rl("world_gen/empty_nether_colony"), chunkPos, cir);
		}

	}

	private void testAndCancel(RandomState randomState, ResourceLocation randomKey, ChunkPos chunkPos, CallbackInfoReturnable<Boolean> cir)
	{
		boolean test = this.test(randomState, randomKey, chunkPos);

		if (!test)
		{
			cir.setReturnValue(false);
		}

	}

	private boolean test(RandomState randomState, ResourceLocation randomKey, ChunkPos chunkPos)
	{
		double chance = MineColoniesTweaksConfigCommon.INSTANCE.worldGens.emptyColoniesGenerationChance.get();

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
			BlockPos chunkBlockPos = chunkPos.getBlockAt(0, 0, 0);
			RandomSource random = randomState.getOrCreateRandomFactory(randomKey).at(chunkBlockPos);
			double next = random.nextDouble();

			return 0 <= next && next <= chance;
		}

	}

}
