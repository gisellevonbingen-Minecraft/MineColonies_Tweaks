package steve_gall.minecolonies_tweaks.mixin.common.minecolonies;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.research.IResearchEffect;
import com.minecolonies.core.research.GlobalResearchEffect;
import com.minecolonies.core.research.ResearchEffectManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import steve_gall.minecolonies_tweaks.api.common.research.ResearchEffectChangedEventArgs;
import steve_gall.minecolonies_tweaks.core.common.research.ResearchEffectManagerExtension;

@Mixin(value = ResearchEffectManager.class, remap = false)
public abstract class ResearchEffectManagerMixin implements ResearchEffectManagerExtension
{
	@Unique
	private IColony minecolonies_tweaks$colony;

	@Shadow(remap = false)
	private Map<ResourceLocation, IResearchEffect> effectMap;

	@Shadow(remap = false)
	abstract double getEffectStrength(ResourceLocation id);

	@Unique
	private boolean minecolonies_tweaks$isServerSide()
	{
		var level = this.minecolonies_tweaks$colony.getWorld();
		return level != null && !level.isClientSide();
	}

	@Inject(method = "applyEffect", remap = false, at = @At(value = "HEAD"), cancellable = false)
	private void applyEffect(IResearchEffect effect, CallbackInfo ci)
	{
		if (this.minecolonies_tweaks$isServerSide() && effect instanceof GlobalResearchEffect effect2)
		{
			var id = effect2.getId();
			var prev = this.getEffectStrength(id);
			var next = effect2.getEffect();

			if (!this.effectMap.containsKey(id) || prev != next)
			{
				MinecraftForge.EVENT_BUS.post(new ResearchEffectChangedEventArgs(this.minecolonies_tweaks$colony, effect2, prev, next));
			}

		}

	}

	@Inject(method = "removeAllEffects", remap = false, at = @At(value = "HEAD"), cancellable = false)
	private void removeAllEffects(CallbackInfo ci)
	{
		if (this.minecolonies_tweaks$isServerSide())
		{
			for (var entry : this.effectMap.entrySet())
			{
				if (entry.getValue() instanceof GlobalResearchEffect effect2)
				{
					var prev = this.getEffectStrength(effect2.getId());
					MinecraftForge.EVENT_BUS.post(new ResearchEffectChangedEventArgs(this.minecolonies_tweaks$colony, effect2, prev, 0.0D));
				}

			}

		}

	}

	@Override
	public void minecolonies_tweaks$setColony(IColony colony)
	{
		this.minecolonies_tweaks$colony = colony;
	}

}
