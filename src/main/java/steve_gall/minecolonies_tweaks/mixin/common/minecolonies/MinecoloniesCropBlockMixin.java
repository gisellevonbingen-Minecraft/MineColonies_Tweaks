package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.List;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.client.VanillaParticleMessage;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.blocks.AbstractBlockMinecolonies;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
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

	@WrapOperation(method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;of()Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;"
			))
	private static BlockBehaviour.Properties init_injectRandomTicks(Operation<Properties> original) {
		return original.call().randomTicks();
	}

	@WrapOperation(method = "attemptGrow",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
					ordinal = 0
			))
	private boolean attemptGrow_sendForgeEvents1(ServerLevel level, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
		return call_sendForgeEvents(level, pos, state, i, original);
	}

	@WrapOperation(method = "attemptGrow",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
					ordinal = 1
			))
	private boolean attemptGrow_sendForgeEvents2(ServerLevel level, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
		return call_sendForgeEvents(level, pos, state, i, original);
	}

	@Unique
	private boolean call_sendForgeEvents(ServerLevel level, BlockPos pos, BlockState state, int i, Operation<Boolean> original) {
		if (MCTweaksConfigServer.INSTANCE.blocks.sendForgeEvents.get()) {
			boolean placed = false;
			if (ForgeHooks.onCropsGrowPre(level, pos, state, true)) {
				placed = original.call(level, pos, state, i);
				ForgeHooks.onCropsGrowPost(level, pos, state);
			}
			return placed;
		}
		else {
			return original.call(level, pos, state, i);
		}
	}

	@Shadow(remap = false)
	public abstract void attemptGrow(BlockState state, ServerLevel level, BlockPos pos);

	@Shadow(remap = false)
	public abstract boolean isMaxAge(BlockState state);

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClientSide)
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

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (MCTweaksConfigServer.INSTANCE.blocks.allowVanillaRandomTicks.get()) {
			// Same implementation as in the last part of MinecoloniesFarmland.randomTick
			int growthChance = 4;
			if (level.isRaining()) {
				growthChance = 6;
			}

			if (random.nextInt(100) <= growthChance) {
				this.attemptGrow(state, level, pos);
				Network.getNetwork().sendToPosition(new VanillaParticleMessage((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() - 0.5F), (double) ((float) pos.getZ() + 0.5F), ParticleTypes.HAPPY_VILLAGER), new PacketDistributor.TargetPoint((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), 16.0, level.dimension()));
			}
		}
    }
}
