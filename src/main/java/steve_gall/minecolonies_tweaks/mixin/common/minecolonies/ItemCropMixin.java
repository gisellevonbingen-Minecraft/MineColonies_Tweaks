package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
import com.minecolonies.core.items.ItemCrop;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import steve_gall.minecolonies_tweaks.core.common.config.MCTweaksConfigServer;
import steve_gall.minecolonies_tweaks.core.common.item.ItemCropExtension;

@Mixin(value = ItemCrop.class, remap = false)
public abstract class ItemCropMixin extends BlockItem implements ItemCropExtension
{
	@Final
	@Mutable
	@Shadow(remap = false)
	private TagKey<Biome> preferredBiome;
	private TagKey<Biome> minecolonies_tweaks$preferredBiome;

	public ItemCropMixin(Block p_40565_, Properties p_40566_)
	{
		super(p_40565_, p_40566_);
	}

	@Inject(method = "<init>", remap = false, at = @At(value = "TAIL"))
	public void init(MinecoloniesCropBlock cropBlock, Properties builder, TagKey<Biome> preferredBiome, CallbackInfo ci)
	{
		this.minecolonies_tweaks$preferredBiome = preferredBiome;
	}

	@Override
	public void minecolonies_tweaks$onServerConfigReloaded()
	{
		this.preferredBiome = MCTweaksConfigServer.INSTANCE.blocks.cropIgnoreBiome.get().booleanValue() ? null : this.minecolonies_tweaks$preferredBiome;
	}

	@WrapOperation(method = "canPlace", remap = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isCreative()Z", remap = true))
	private boolean canPlace_isCreative(Player player, Operation<Boolean> operation)
	{
		if (MCTweaksConfigServer.INSTANCE.blocks.cropCanPlayerPlant.get().booleanValue())
		{
			return true;
		}
		else
		{
			return operation.call(player);
		}

	}

	@Inject(method = "appendHoverText", remap = true, at = @At(value = "RETURN"), cancellable = true)
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci)
	{
		for (var i = 0; i < tooltip.size(); i++)
		{
			var line = tooltip.get(i);

			if (this.testForRemove(line))
			{
				tooltip.remove(i);
				i--;
			}

		}

	}

	private boolean testForRemove(Component line)
	{
		var config = MCTweaksConfigServer.INSTANCE.blocks;

		if (config.cropCanPlayerPlant.get().booleanValue() && config.cropVanillaFarmland.get().booleanValue())
		{
			return line.getContents() instanceof TranslatableContents contents && contents.getKey().equals(TranslationConstants.CROP_TOOLTIP);
		}

		return false;
	}

}
