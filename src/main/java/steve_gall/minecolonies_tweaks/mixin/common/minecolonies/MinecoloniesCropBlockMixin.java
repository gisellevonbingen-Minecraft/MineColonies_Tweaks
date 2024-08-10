package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
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
import steve_gall.minecolonies_tweaks.core.common.config.MineColoniesTweaksConfigServer;

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
	private void init(String blockName, Block preferredFarmland, TagKey<Biome> preferredBiome, CallbackInfo ci)
	{
		this.minecolonies_tweaks$preferredFarmland = preferredFarmland;
		this.minecolonies_tweaks$preferredBiome = preferredBiome;
	}

	@Shadow(remap = false)
	public abstract void attemptGrow(BlockState state, ServerLevel level, BlockPos pos);

	@Shadow(remap = false)
	public abstract boolean isMaxAge(BlockState state);

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClientSide)
	{
		return MineColoniesTweaksConfigServer.INSTANCE.blocks.cropCanPerformBonemeal.get().booleanValue() && !this.isMaxAge(state);
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
		this.preferredFarmland = MineColoniesTweaksConfigServer.INSTANCE.blocks.cropVanillaFarmland.get().booleanValue() ? Blocks.FARMLAND : this.minecolonies_tweaks$preferredFarmland;
		this.preferredBiome = MineColoniesTweaksConfigServer.INSTANCE.blocks.cropIgnoreBiome.get().booleanValue() ? null : this.minecolonies_tweaks$preferredBiome;
	}

}
