package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import static com.minecolonies.api.util.constant.CitizenConstants.BLOCK_BREAK_SOUND_RANGE;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.api.blocks.AbstractBlockMinecolonies;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
import com.minecolonies.core.blocks.MinecoloniesFarmland;
import com.minecolonies.core.network.messages.client.VanillaParticleMessage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import steve_gall.minecolonies_tweaks.core.common.block.MinecoloniesCropBlockExtension;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;

@Mixin(value = MinecoloniesCropBlock.class, remap = false)
public abstract class MinecoloniesCropBlockMixin extends AbstractBlockMinecolonies<MinecoloniesCropBlock> implements MinecoloniesCropBlockExtension, BonemealableBlock
{
	@Final
	@Mutable
	@Shadow(remap = false)
	private Block preferredFarmland;
	private Block minecolonies_tweaks$preferredFarmland;

	@Final
	@Mutable
	@Shadow(remap = false)
	private TagKey<Biome> preferredBiome;
	private TagKey<Biome> minecolonies_tweaks$preferredBiome;

	public MinecoloniesCropBlockMixin(Properties properties)
	{
		super(properties);
	}

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"))
	private void init(String blockName, Block preferredFarmland, List<Block> droppedFrom, TagKey<Biome> preferredBiome, CallbackInfo ci)
	{
		this.minecolonies_tweaks$preferredFarmland = preferredFarmland;
		this.minecolonies_tweaks$preferredBiome = preferredBiome;
	}

	@WrapOperation(method = "<init>", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;of()Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", remap = true))
	private static BlockBehaviour.Properties init_injectRandomTicks(Operation<Properties> original)
	{
		return original.call().randomTicks();
	}

	@WrapOperation(method = "attemptGrow", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", remap = true, ordinal = 0))
	private boolean attemptGrow_sendForgeEvents1(ServerLevel level, BlockPos pos, BlockState state, int i, Operation<Boolean> original)
	{
		return this.minecolonies_tweaks$call_sendForgeEvents(level, pos, state, i, original);
	}

	@WrapOperation(method = "attemptGrow", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", remap = true, ordinal = 1))
	private boolean attemptGrow_sendForgeEvents2(ServerLevel level, BlockPos pos, BlockState state, int i, Operation<Boolean> original)
	{
		return this.minecolonies_tweaks$call_sendForgeEvents(level, pos, state, i, original);
	}

	@Unique
	private boolean minecolonies_tweaks$call_sendForgeEvents(ServerLevel level, BlockPos pos, BlockState state, int i, Operation<Boolean> original)
	{
		var placed = false;

		if (CommonHooks.canCropGrow(level, pos, state, true))
		{
			placed = original.call(level, pos, state, i);
			CommonHooks.fireCropGrowPost(level, pos, state);
		}

		return placed;
	}

	@Shadow(remap = false)
	public abstract void attemptGrow(BlockState state, ServerLevel level, BlockPos pos);

	@Shadow(remap = false)
	public abstract boolean isMaxAge(BlockState state);

	@Override
	@Deprecated
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		super.randomTick(state, level, pos, random);

		if (MCTweaksConfigServer.INSTANCE.blocks.cropVanillaFarmland.get() && !this.isMaxAge(state) && !(level.getBlockState(pos.below()).getBlock() instanceof MinecoloniesFarmland))
		{
			var growthChance = level.isRaining() ? 6 : 4;

			if (random.nextInt(100) <= growthChance)
			{
				this.attemptGrow(state, level, pos);
				PacketDistributor.sendToPlayersNear(level, null, pos.getX(), pos.getY(), pos.getZ(), BLOCK_BREAK_SOUND_RANGE, new VanillaParticleMessage(pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, ParticleTypes.HAPPY_VILLAGER));
			}

		}

	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state)
	{
		return MCTweaksConfigServer.INSTANCE.blocks.cropCanPerformBonemeal.get().booleanValue() && !this.isMaxAge(state);
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state)
	{
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state)
	{
		this.attemptGrow(state, level, pos);
	}

	@Override
	public void minecolonies_tweaks$onServerConfigReloaded()
	{
		this.preferredFarmland = MCTweaksConfigServer.INSTANCE.blocks.cropVanillaFarmland.get().booleanValue() ? Blocks.FARMLAND : this.minecolonies_tweaks$preferredFarmland;
		this.preferredBiome = MCTweaksConfigServer.INSTANCE.blocks.cropIgnoreBiome.get().booleanValue() ? null : this.minecolonies_tweaks$preferredBiome;
	}

}
